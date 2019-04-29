/* Copyright (c) 2001-2019 by SAP SE, Walldorf, Germany.
 * All rights reserved. Confidential and proprietary.
 */
package my.bookshop;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import com.sap.cloud.sdk.service.prov.api.DataSourceHandler;
import com.sap.cloud.sdk.service.prov.api.EntityData;
import com.sap.cloud.sdk.service.prov.api.ExtensionHelper;
import com.sap.cloud.sdk.service.prov.api.annotations.BeforeUpdate;
import com.sap.cloud.sdk.service.prov.api.exits.BeforeUpdateResponse;
import com.sap.cloud.sdk.service.prov.api.internal.DefaultEntityData;
import com.sap.cloud.sdk.service.prov.api.internal.HasMetadata;
import com.sap.cloud.sdk.service.prov.api.request.UpdateRequest;

/**
 * Custom handler for the Admin Service
 */
public class AdminService {
    
    /**
     * The string identifying the price column of the Books table
     */
    private static final String PRICE = "price";
    
    /**
     * The string identifying the ID column of the Books table
     */
    private static final String ID = "ID";
    
    /**
     * The string identifying the amount column of the OrderItems table
     */
    private static final String AMOUNT = "amount";
    
    /**
     * The string identifying the netAmount column of the OrderItems table
     */
    private static final String NET_AMOUNT = "netAmount";
    
    /**
     * The string identifying the book_ID column of the OrderItems table
     */
    private static final String BOOK_ID = "book_ID";
    
    /**
     * The string identifying the Books entity
     */
    private static final String BOOKS = "Books";
    
    @BeforeUpdate(entity = "OrderItems", serviceName = "AdminService")
    public BeforeUpdateResponse updateOrderItems(UpdateRequest req, ExtensionHelper extensionHelper) throws Exception {
        EntityData orderItem = req.getData();
        // check if amount was updated
        Integer amount = (Integer) orderItem.getElementValue(AMOUNT);
        if(amount == null) {
            return BeforeUpdateResponse.setSuccess().response();
        }
        
        Map<String, Object> itemMap = orderItem.asMap();
        DataSourceHandler handler = extensionHelper.getHandler();
        // get the book price and calculate the netAmount of the order item
        Integer id = (Integer) orderItem.getElementValue(BOOK_ID);
        EntityData book = handler.executeRead(BOOKS, Collections.singletonMap(ID, id), Arrays.asList(PRICE));
        double price = ((BigDecimal) book.getElementValue(PRICE)).doubleValue();
        itemMap.put(NET_AMOUNT, price * amount);
        EntityData result = new DefaultEntityData(itemMap, ((HasMetadata) orderItem).getEntityMetadata());
        
        return BeforeUpdateResponse.setSuccess().setData(result).response();
    }

}
