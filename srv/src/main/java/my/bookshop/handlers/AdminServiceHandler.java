/* Copyright (c) 2001-2019 by SAP SE, Walldorf, Germany.
 * All rights reserved. Confidential and proprietary.
 */
package my.bookshop.handlers;

import java.math.BigDecimal;
import java.util.function.Supplier;
import java.util.stream.Stream;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sap.cds.Result;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Update;
import com.sap.cds.services.ErrorStatuses;
import com.sap.cds.services.ServiceException;
import com.sap.cds.services.cds.CdsService;
import com.sap.cds.services.draft.DraftCancelEventContext;
import com.sap.cds.services.draft.DraftPatchEventContext;
import com.sap.cds.services.draft.DraftService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.Before;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.messages.Messages;

import cds.gen.adminservice.AdminService_;
import cds.gen.adminservice.Books;
import cds.gen.adminservice.Books_;
import cds.gen.adminservice.OrderItems;
import cds.gen.adminservice.OrderItems_;
import cds.gen.adminservice.Orders;
import cds.gen.adminservice.Orders_;
import my.bookshop.MessageKeys;


/**
 * Custom business logic for the "Admin Service" (see admin-service.cds)
 *
 * Handles creating and editing orders.
 */
@Component
@ServiceName(AdminService_.CDS_NAME)
public class AdminServiceHandler implements EventHandler {

	@Resource(name = AdminService_.CDS_NAME)
	private DraftService adminService;

	@Autowired
	private Messages messages;

	/**
	 * Finish an order
	 * @param orders
	 */
	@Before(event = { CdsService.EVENT_CREATE, CdsService.EVENT_UPSERT })
	public void beforeCreateOrder(Stream<Orders> orders) {
		orders.forEach(order -> {
			// reset total
			order.setTotal(BigDecimal.valueOf(0));
			order.getItems().forEach(orderItem -> {
				// validation of the request
				Integer amount = orderItem.getAmount();
				if (amount == null || amount <= 0) {
					// exceptions with localized messages from property files
					// exceptions abort the request and set an error http status code
					throw new ServiceException(ErrorStatuses.BAD_REQUEST, MessageKeys.AMOUNT_REQUIRE_MINIMUM);
				}

				String id = orderItem.getBookId();
				if (id == null) {
					// using static text without localization is still possible in exceptions and messages
					throw new ServiceException(ErrorStatuses.BAD_REQUEST, "You have to specify the book to order");
				}

				// check if enough books are available
				Result result = adminService.run(Select.from(Books_.class).columns(b -> b.ID(), b -> b.stock(), b -> b.price()).byId(id));
				Books book = result.first(Books.class).orElseThrow(notFound(MessageKeys.BOOK_MISSING));
				if (book.getStock() < amount) {
					throw new ServiceException(ErrorStatuses.BAD_REQUEST, MessageKeys.BOOK_REQUIRE_STOCK);
				}

				// update the net amount
				BigDecimal updatedNetAmount = book.getPrice().multiply(BigDecimal.valueOf(amount));
				orderItem.setNetAmount(updatedNetAmount);
				// update the total
				order.setTotal(order.getTotal().add(updatedNetAmount));

				// update the book with the new stock
				// FIXME this should only update the diff amount and handle book changes
				book.setStock(book.getStock() - amount);
				adminService.run(Update.entity(Books_.class).data(book));
			});
		});
	}

	/**
	 * Calculate the net amount and total preview updated while editing an order
	 * @param context
	 * @param orderItems
	 */
	@Before(event = DraftService.EVENT_DRAFT_PATCH)
	public void patchOrderItems(DraftPatchEventContext context, Stream<OrderItems> orderItems) {
		orderItems.forEach(orderItem -> {
			// check if amount or book was updated
			Integer amount = orderItem.getAmount();
			String bookId = orderItem.getBookId();
			String orderItemId = context.getParameterInfo().getQueryParameter(OrderItems.ID); // FIXME get from CQN
			BigDecimal netAmount = calculateNetAmountInDraft(orderItemId, amount, bookId);
			if(netAmount != null) {
				orderItem.setNetAmount(netAmount);
			}
		});
	}

	/**
	 * Calculate total preview when an order item is deleted
	 * @param context
	 */
	@Before(event = DraftService.EVENT_DRAFT_CANCEL, entity = OrderItems_.CDS_NAME)
	public void cancelOrderItems(DraftCancelEventContext context) {
		String orderItemId = context.getParameterInfo().getQueryParameter(OrderItems.ID); // FIXME get from CQN
		calculateNetAmountInDraft(orderItemId, 0, null);
	}

	private BigDecimal calculateNetAmountInDraft(String orderItemId, Integer newAmount, String newBookId) {
		Integer amount = newAmount;
		String bookId = newBookId;
		if(amount == null && bookId == null) {
			return null; // nothing changed
		}

		// get the order item that was updated (to get access to the book price, amount and order total)
		Result result = adminService.run(Select.from(OrderItems_.class)
				.columns(o -> o.amount(), o -> o.netAmount(),
						o -> o.book().expand(b -> b.ID(), b -> b.price()),
						o -> o.parent().expand(p -> p.ID(), p -> p.total()))
				.where(o -> o.ID().eq(orderItemId).and(o.IsActiveEntity().eq(false))));
		OrderItems itemToPatch = result.first(OrderItems.class).orElseThrow(notFound(MessageKeys.ORDERITEM_MISSING));
		BigDecimal bookPrice = null;

		// fallback to existing values
		if(amount == null) {
			amount = itemToPatch.getAmount();
		}

		if(bookId == null && itemToPatch.getBook() != null) {
			bookId = itemToPatch.getBook().getId();
			bookPrice = itemToPatch.getBook().getPrice();
		}

		if(amount == null || bookId == null) {
			return null; // not enough data available
		}

		// only warn about invalid values in draft mode
		if(amount <= 0) {
			// additional messages with localized messages from property files
			// these messages are transported in sap-messages and do not abort the request
			messages.warn(MessageKeys.AMOUNT_REQUIRE_MINIMUM);
		}

		// get the price of the updated book ID
		if(bookPrice == null) {
			result = adminService.run(Select.from(Books_.class).byId(bookId).columns(b -> b.price()));
			Books book = result.first(Books.class).orElseThrow(notFound(MessageKeys.BOOK_MISSING));
			bookPrice = book.getPrice();
		}

		// update the net amount of the order item
		BigDecimal updatedNetAmount = bookPrice.multiply(BigDecimal.valueOf(amount));

		// update the order's total
		BigDecimal previousNetAmount = defaultZero(itemToPatch.getNetAmount());
		BigDecimal currentTotal = defaultZero(itemToPatch.getParent().getTotal());
		BigDecimal newTotal = currentTotal.subtract(previousNetAmount).add(updatedNetAmount);
		adminService.patchDraft(Update.entity(Orders_.class)
				.where(o -> o.ID().eq(itemToPatch.getParent().getId()).and(o.IsActiveEntity().eq(false)))
				.data(Orders.TOTAL, newTotal));

		return updatedNetAmount;
	}

	private Supplier<ServiceException> notFound(String message) {
		return () -> new ServiceException(ErrorStatuses.NOT_FOUND, message);
	}

	private BigDecimal defaultZero(BigDecimal decimal) {
		return decimal == null ? BigDecimal.valueOf(0) : decimal;
	}

}
