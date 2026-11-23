
package org.demo.springretry.service;

import org.demo.springretry.entity.Order;
import org.demo.springretry.exception.OrderTemporaryException;
import org.demo.springretry.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class OrderPersistenceService {

    private final OrderRepository orderRepository;

    private final AtomicInteger failureCounter = new AtomicInteger(0);

    public OrderPersistenceService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public Order saveOrder(
            String productName,
            Integer quantity,
            BigDecimal amount) {
        int attempt = failureCounter.incrementAndGet();

        System.out.println("Database operation attempt: " + attempt);

        Order order = new Order(productName, quantity, amount);

        Order savedOrder = orderRepository.save(order);

        if (attempt <= 2) {
            System.out.println("Throwing OrderTemporaryException");
            throw new OrderTemporaryException(
                    "Temporary database failure"
            );
        }
        return savedOrder;
    }
}
