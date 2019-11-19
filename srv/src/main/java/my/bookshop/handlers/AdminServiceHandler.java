/* Copyright (c) 2001-2019 by SAP SE, Walldorf, Germany.
 * All rights reserved. Confidential and proprietary.
 */
package my.bookshop.handlers;

import java.math.BigDecimal;
import java.util.function.Supplier;
import java.util.stream.Stream;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import com.sap.cds.Result;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Update;
import com.sap.cds.services.ErrorStatuses;
import com.sap.cds.services.ServiceException;
import com.sap.cds.services.cds.CdsCreateEventContext;
import com.sap.cds.services.cds.CdsService;
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
@RequestScope
@ServiceName(AdminService_.CDS_NAME)
public class AdminServiceHandler implements EventHandler {

	/**
	 * The logger
	 */
	private static final Logger LOG = LoggerFactory.getLogger(AdminServiceHandler.class.getName());

	@Resource(name = AdminService_.CDS_NAME)
	private CdsService adminService;


	@Before(event = { CdsService.EVENT_CREATE }, entity = { Orders_.CDS_NAME } )
	public void beforeCreateOrder(CdsCreateEventContext event, Stream<Orders> orders) throws Exception {
		orders.forEach(order -> {
			order.getItems().forEach(orderItem -> {
				LOG.info("Creating Order Item");
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

				// FIXME: this should not be necessary, as the net amount was already calculated in the
				//   Before DRAFT_PATCH handler below. Unfortunately, the value will be set to null again,
				//   because the field is marked read-only.
				// update the net amount
				BigDecimal updatedNetAmount = book.getPrice().multiply(BigDecimal.valueOf(orderItem.getAmount()));
				orderItem.setNetAmount(updatedNetAmount);

				// update the book with the new stock
				book.setStock(book.getStock() - amount);
				// catalogService.run(Update.entity(Books_.class).byId(book.getId()).data(book));
				adminService.run(Update.entity(Books_.class).byId(book.getId()).data(book));
			});
		});
	}


	@Before(event = { DraftService.EVENT_DRAFT_PATCH })
	public void patchOrderItems(DraftPatchEventContext event, Stream<OrderItems> orderItems) throws Exception {
		orderItems.forEach(orderItem -> {
			// check if amount was updated
			Integer amount = orderItem.getAmount();
			if(amount == null) {
				return;
			}

			// get the order item that was updated (to get access to the book ID)
			String orderItemId = event.getParameterInfo().getQueryParameter("ID");
			Result result = adminService.run(Select.from(OrderItems_.class).byId(orderItemId));
			OrderItems itemToPatch = result.first(OrderItems.class).orElseThrow(notFound("OrderItem does not exist"));

			// get the book for the book ID to calculate the net amount from the book price
			result = adminService.run(Select.from(Books_.class).byId(itemToPatch.getBookId()));
			Books book = result.first(Books.class).orElseThrow(notFound("Book does not exist"));

			// update the net amount of the order item
			BigDecimal updatedNetAmount = book.getPrice().multiply(BigDecimal.valueOf(amount));
			orderItem.setNetAmount(updatedNetAmount);
		});
	}


	private Supplier<ServiceException> notFound(String message) {
		return () -> new ServiceException(ErrorStatuses.NOT_FOUND, message);
	}

}
