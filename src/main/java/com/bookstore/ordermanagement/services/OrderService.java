package com.bookstore.ordermanagement.services;

import com.bookstore.ordermanagement.dao.OrderRepository;
import com.bookstore.ordermanagement.entities.BookDetail;
import com.bookstore.ordermanagement.entities.Order;
import com.bookstore.ordermanagement.models.Book;
import com.bookstore.ordermanagement.models.OrderSummary;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
@Component
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    ObjectMapper objectMapper;

    /**
     * Retrieves all orders.
     *
     * @return the list of orders
     */
    public List<Order> getAllOrders() {
        return (List<Order>) orderRepository.findAll();
    }

    /**
     * Retrieves an order by its ID.
     *
     * @param id the ID of the order to retrieve
     * @return the order if found, or null if not found
     */
    public Order getOrderById(int id) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        return optionalOrder.orElse(null);
    }

    /**
     * Creates a new order. Validate if all books ordered are in stock
     *
     * @param order the order to create
     * @return the created order
     * @throws IllegalArgumentException if any book in the order is not in stock
     */
    public OrderSummary placeOrder(Order order) throws IllegalArgumentException, IOException {

        List<BookDetail> orderedBooks = order.getBookDetails();
        double totalAmount = 0;

        // Make PUT API call to Book service to update and get book details
        String url = "http://localhost:8080/books/order";
        ResponseEntity<JsonNode> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.PUT,
                new HttpEntity<>(orderedBooks),
                JsonNode.class
        );

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            JsonNode jsonNode = objectMapper.readTree(Objects.requireNonNull(responseEntity.getBody()).toString());
            List<Book> books = objectMapper.convertValue(jsonNode, new TypeReference<List<Book>>() {});

            for(Book book : books) {
                int orderedQuantity = orderedBooks.stream()
                        .filter(bookDetails -> bookDetails.getBookId() == book.getId())
                        .findFirst()
                        .get()
                        .getOrderedQuantity();
                totalAmount = totalAmount + book.getPrice()*orderedQuantity;
                book.setQuantity(orderedQuantity);
            }

            order.setTotalAmount(totalAmount);
            log.info("Order executed successfully");
            Order savedOrder = orderRepository.save(order);

            OrderSummary orderSummary = OrderSummary.builder()
                    .id(savedOrder.getId())
                    .customerName(savedOrder.getCustomerName())
                    .address(savedOrder.getAddress())
                    .totalAmount(savedOrder.getTotalAmount())
                    .books(books)
                    .build();
            return orderSummary;
        } else {
            String errorMessage = Objects.requireNonNull(responseEntity.getBody()).toString();
            throw new IllegalArgumentException(errorMessage);
        }
    }

    /**
     * Deletes an order and adds the books back to inventory
     *
     * @param id the ID of the order to delete
     * @return true if the order is deleted, false if not found
     */
    public boolean deleteOrder(int id) {
        Optional<Order> optionalOrder = orderRepository.findById(id);

        if (optionalOrder.isPresent()) {
            orderRepository.deleteById(id);

            Order deletedOrder = optionalOrder.get();

            // add books back in book inventory
            List<BookDetail> orderedBooks = deletedOrder.getBookDetails();

            // Make PUT API call to Book service to update and get book details
            String url = "http://localhost:8080/books/return";
            ResponseEntity<Boolean> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.PUT,
                    new HttpEntity<>(orderedBooks),
                    boolean.class
            );

            return responseEntity.getStatusCode() == HttpStatus.OK;
        } else {
            return false;
        }
    }

}
