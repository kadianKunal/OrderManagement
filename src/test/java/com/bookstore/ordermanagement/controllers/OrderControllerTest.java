package com.bookstore.ordermanagement.controllers;

import com.bookstore.ordermanagement.entities.Order;
import com.bookstore.ordermanagement.models.OrderSummary;
import com.bookstore.ordermanagement.services.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllOrders() {
        // Mocking the orderService.getAllOrders() method
        Order order1 = new Order();
        Order order2 = new Order();
        List<Order> orders = Arrays.asList(order1, order2);
        when(orderService.getAllOrders()).thenReturn(orders);

        // Calling the getAllOrders() method of the orderController
        ResponseEntity<List<Order>> response = orderController.getAllOrders();

        // Verifying the result
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(orders, response.getBody());
    }

    @Test
    public void testGetOrderById_ExistingOrder() {
        int orderId = 1;
        Order order = new Order();
        when(orderService.getOrderById(orderId)).thenReturn(order);

        ResponseEntity<Order> response = orderController.getOrderById(orderId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(order, response.getBody());
    }

    @Test
    public void testGetOrderById_NonExistingOrder() {
        int orderId = 1;
        when(orderService.getOrderById(orderId)).thenReturn(null);

        ResponseEntity<Order> response = orderController.getOrderById(orderId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testCreateOrder_Success() throws IOException {
        Order order = new Order();
        OrderSummary orderSummary = new OrderSummary();

        when(orderService.placeOrder(order)).thenReturn(orderSummary);

        ResponseEntity<Object> response = orderController.createOrder(order);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(orderSummary, response.getBody());
    }

    @Test
    public void testCancelOrder_ExistingOrder() {
        int orderId = 1;

        when(orderService.deleteOrder(orderId)).thenReturn(true);

        ResponseEntity<Void> response = orderController.cancelOrder(orderId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void testCancelOrder_NonExistingOrder() {
        int orderId = 1;

        when(orderService.deleteOrder(orderId)).thenReturn(false);

        ResponseEntity<Void> response = orderController.cancelOrder(orderId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

}
