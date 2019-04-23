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
 * @author Marcel Merkle
 */
public class AdminService {

    
    @BeforeUpdate(entity = "OrderItems", serviceName = "AdminService")
    public BeforeUpdateResponse updateOrderItems(UpdateRequest req, ExtensionHelper extensionHelper) throws Exception {
        EntityData orderItem = req.getData();
        // check if amount was updated
        Integer amount = (Integer) orderItem.getElementValue("amount");
        if(amount == null) {
            return BeforeUpdateResponse.setSuccess().response();
        }
        
        Map<String, Object> itemMap = orderItem.asMap();
        DataSourceHandler handler = extensionHelper.getHandler();
        // get the book price and calculate the netAmount of the order item
        Integer id = (Integer) orderItem.getElementValue("book_ID");
        EntityData book = handler.executeRead("Books", Collections.singletonMap("ID", id), Arrays.asList("ID", "price"));
        double price = ((BigDecimal) book.getElementValue("price")).doubleValue();
        itemMap.put("netAmount", price * amount);
        EntityData result = new DefaultEntityData(itemMap, ((HasMetadata) orderItem).getEntityMetadata());
        
        return BeforeUpdateResponse.setSuccess().setData(result).response();
    }

}
