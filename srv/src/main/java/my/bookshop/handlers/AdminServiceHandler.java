/* Copyright (c) 2001-2019 by SAP SE, Walldorf, Germany.
 * All rights reserved. Confidential and proprietary.
 */
package my.bookshop.handlers;

import java.math.BigDecimal;
import java.util.function.Supplier;
import java.util.stream.Stream;

import javax.annotation.Resource;

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

import adminservice.AdminService_;
import adminservice.Books;
import adminservice.Books_;
import adminservice.OrderItems;
import adminservice.OrderItems_;
import adminservice.Orders;
import adminservice.Orders_;


/**
 * Custom handler for the Admin Service
 */
@Component
@ServiceName(AdminService_.CDS_NAME)
public class AdminServiceHandler implements EventHandler {

	@Resource(name = AdminService_.CDS_NAME)
	private DraftService adminService;

	@Before(event = { CdsService.EVENT_CREATE, CdsService.EVENT_UPSERT })
	public void beforeCreateOrder(Stream<Orders> orders) {
		orders.forEach(order -> {
			// reset total
			order.setTotal(BigDecimal.valueOf(0));
			order.getItems().forEach(orderItem -> {
				// validation of the request
				Integer amount = orderItem.getAmount();
				if (amount == null || amount <= 0) {
					throw new ServiceException(ErrorStatuses.BAD_REQUEST, "Order at least 1 book");
				}

				String id = orderItem.getBookId();
				if (id == null) {
					throw new ServiceException(ErrorStatuses.BAD_REQUEST, "You have to specify the book to order");
				}

				// check if enough books are available
				Result result = adminService.run(Select.from(Books_.class).byId(id));
				Books book = result.first(Books.class).orElseThrow(notFound("Book does not exist"));
				if (book.getStock() < amount) {
					throw new ServiceException(ErrorStatuses.BAD_REQUEST, "Not enough books on stock");
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

	@Before(event = DraftService.EVENT_DRAFT_PATCH)
	public void patchOrderItems(DraftPatchEventContext event, Stream<OrderItems> orderItems) {
		orderItems.forEach(orderItem -> {
			// check if amount or book was updated
			Integer amount = orderItem.getAmount();
			String bookId = orderItem.getBookId();
			String orderItemId = event.getParameterInfo().getQueryParameter(OrderItems.ID); // FIXME get from CQN
			BigDecimal netAmount = calculateNetAmountInDraft(orderItemId, amount, bookId);
			if(netAmount != null) {
				orderItem.setNetAmount(netAmount);
			}
		});
	}

	@Before(event = DraftService.EVENT_DRAFT_CANCEL, entity = OrderItems_.CDS_NAME)
	public void cancelOrderItems(DraftCancelEventContext event) {
		String orderItemId = event.getParameterInfo().getQueryParameter(OrderItems.ID); // FIXME get from CQN
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
		OrderItems itemToPatch = result.first(OrderItems.class).orElseThrow(notFound("OrderItem does not exist"));
		BigDecimal bookPrice = null;

		// fallback to existing values
		if(amount == null) {
			amount = itemToPatch.getAmount();
		}

		if(bookId == null) {
			bookId = itemToPatch.getBook().getId();
			bookPrice = itemToPatch.getBook().getPrice();
		}

		if(amount == null || bookId == null) {
			return null; // not enough data available
		}

		// get the price of the updated book ID
		if(bookPrice == null) {
			result = adminService.run(Select.from(Books_.class).byId(bookId).columns(b -> b.price()));
			Books book = result.first(Books.class).orElseThrow(notFound("Book does not exist"));
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
