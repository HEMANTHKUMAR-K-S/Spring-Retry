package org.demo.springretry.service;

import org.demo.springretry.entity.Order;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.retry.RetryTemplate;
import org.springframework.stereotype.Service;


@Service
public class OrderService {

    private final OrderPersistenceService orderPersistenceService;
    private final RetryTemplate retryTemplate;


    public OrderService(OrderPersistenceService orderPersistenceService, @Qualifier("orderRetryTemplate") RetryTemplate retryTemplate) {
        this.orderPersistenceService = orderPersistenceService;
        this.retryTemplate = retryTemplate;
    }

    public Order createOrder(Order order) {

        return retryTemplate.invoke(()-> orderPersistenceService.saveOrder(
                order.getProductName(),
                order.getQuantity(),
                order.getAmount()

        )
        );
    }

}
