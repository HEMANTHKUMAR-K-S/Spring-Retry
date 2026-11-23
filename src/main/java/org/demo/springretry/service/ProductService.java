package org.demo.springretry.service;

import org.demo.springretry.exception.InventoryUnavaiableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);
    private final InventoryService inventoryService;


    public ProductService(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    public String getProduct(String productId){

        try {
            return inventoryService.checkStock(productId);
        }
        catch (InventoryUnavaiableException e){

            log.error("invenotory service is unavailable ",e);
            return "inventory service temporarily unavailable";
        }
    }
}
