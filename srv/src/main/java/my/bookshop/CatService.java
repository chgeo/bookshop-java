/* Copyright (c) 2001-2019 by SAP SE, Walldorf, Germany.
 * All rights reserved. Confidential and proprietary.
 */
package my.bookshop;

import java.util.Arrays;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.cloud.sdk.service.prov.api.DataSourceHandler;
import com.sap.cloud.sdk.service.prov.api.EntityData;
import com.sap.cloud.sdk.service.prov.api.ExtensionHelper;
import com.sap.cloud.sdk.service.prov.api.annotations.BeforeCreate;
import com.sap.cloud.sdk.service.prov.api.exits.BeforeCreateResponse;
import com.sap.cloud.sdk.service.prov.api.internal.DefaultEntityData;
import com.sap.cloud.sdk.service.prov.api.internal.HasMetadata;
import com.sap.cloud.sdk.service.prov.api.request.CreateRequest;
import com.sap.cloud.sdk.service.prov.api.response.ErrorResponse;

/**
 * Custom Handler for the Catalog Service
 */
public class CatService {
    /**
     * The logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(CatService.class.getName());

    /**
     * The string identifying the ID column of the Books table
     */
    private static final String ID = "ID";

    /**
     * The string identifying the amount column of the OrderItems table
     */
    private static final String AMOUNT = "amount";

    /**
     * The string identifying the stock column of the Books table
     */
    private static final String STOCK = "stock";

    @BeforeCreate(entity = "OrderItems", serviceName = "CatalogService")
    public BeforeCreateResponse create(CreateRequest req, ExtensionHelper extensionHelper) throws Exception {
        LOG.info("Creating Order Item");
        // validation of the request
        EntityData orderItem = req.getData();
        Integer amount = (Integer) orderItem.getElementValue(AMOUNT);
        if (amount == null || amount <= 0) {
            return getErrorResponse(400, "Order at least 1 book");
        }
        DataSourceHandler handler = extensionHelper.getHandler();

        Integer id = (Integer) orderItem.getElementValue("book_ID");
        if (id == null) {
            return getErrorResponse(400, "You have to specify the book to order");
        }

        // check if enough books are available
        EntityData book = handler.executeRead("Books", Collections.singletonMap(ID, id), Arrays.asList(ID, STOCK));

        int stock = (Integer) book.getElementValue(STOCK);
        if (stock < amount) {
            return getErrorResponse(400, "Not enough books on stock");
        }

        // update the book with the new stock
        stock -= amount;
        EntityData updatedBook = new DefaultEntityData(Collections.singletonMap(STOCK, stock),
                ((HasMetadata) book).getEntityMetadata());
        handler.executeUpdate(updatedBook, Collections.singletonMap(ID, id), false);

        return BeforeCreateResponse.setSuccess().response();
    }

    /**
     * Helper method to create an error response
     * 
     * @param statusCode the HTTP status code
     * @param msg        the error message
     * @return an {@link ErrorResponse} containing the status code {@code stausCode}
     *         and message {@code msg}
     */
    private static BeforeCreateResponse getErrorResponse(int statusCode, String msg) {
        return BeforeCreateResponse.setError(ErrorResponse.getBuilder().setStatusCode(statusCode).setMessage(msg).response());
    }
}
