package org.demo.springretry.service;

import org.demo.springretry.client.InventoryClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.retry.RetryTemplate;
import org.springframework.stereotype.Service;

@Service
public class InventoryService {

    private final InventoryClient inventoryClient;
    private final RetryTemplate retryTemplate;


    private final Logger log = LoggerFactory.getLogger(InventoryClient.class);

    public InventoryService(InventoryClient inventoryClient, @Qualifier("inventoryRetryTemplate") RetryTemplate retryConfig) {
        this.inventoryClient = inventoryClient;

        this.retryTemplate = retryConfig;
    }


//    @Retryable(includes = InventoryUnavaiableException.class,
//            maxRetriesString = "${retry.inventory.max-attempts}",
//            delayString = "${retry.inventory.delay}",multiplierString = "${retry.inventory.multiplier}",maxDelayString = "${retry.inventory.max-delay}")
    public String checkStock(String productId){
        log.info("checking stock for product:" + productId);
        return  retryTemplate.invoke(()-> inventoryClient.fetchStock(productId));
    }
}
