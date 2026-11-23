package org.demo.springretry.Controller;

import org.demo.springretry.dto.OrderRequest;
import org.demo.springretry.entity.Order;
import org.demo.springretry.repository.OrderRepository;
import org.demo.springretry.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;


    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }


    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody OrderRequest request){

            Order order=new Order(
                    request.productName(),
                    request.quantity(),
                    request.amount());

            Order savedOrders=orderService.createOrder(order);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(order);
    }
}
