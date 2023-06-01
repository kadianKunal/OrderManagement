package com.bookstore.ordermanagement.controllers;

import com.bookstore.ordermanagement.entities.Order;
import com.bookstore.ordermanagement.models.OrderSummary;
import com.bookstore.ordermanagement.services.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * Retrieves all orders.
     *
     * @return the list of orders
     */
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    /**
     * Retrieves an order by its ID.
     *
     * @param id the ID of the order to retrieve
     * @return the order if found, or 404 Not Found if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable int id) {
        Order order = orderService.getOrderById(id);
        if (order != null) {
            return ResponseEntity.ok(order);
        } else {
            log.error("Order not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Creates a new order.
     *
     * @param order the order to create
     * @return the created order
     */
    @PostMapping
    public ResponseEntity<Object> createOrder(@RequestBody Order order) {
        try {
            OrderSummary orderSummary = orderService.placeOrder(order);
            log.info("Order executed with ID: {}", orderSummary.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(orderSummary);
        } catch (Exception e) {
            log.error("Failed to execute order: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Cancels an order and adds the books back to inventory
     *
     * @param id the ID of the order to delete
     * @return 204 No Content if the order is deleted, or 404 Not Found if not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelOrder(@PathVariable int id) {
        try {
            boolean deleted = orderService.deleteOrder(id);
            if (deleted) {
                log.info("Order deleted with ID: {}", id);
                return ResponseEntity.noContent().build();
            } else {
                log.warn("Order not found with ID: {}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Failed to cancel order: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

}
