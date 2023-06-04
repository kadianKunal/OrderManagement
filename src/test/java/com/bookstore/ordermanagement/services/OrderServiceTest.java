package com.bookstore.ordermanagement.services;

import com.bookstore.ordermanagement.dao.OrderRepository;
import com.bookstore.ordermanagement.entities.BookDetail;
import com.bookstore.ordermanagement.entities.Order;
import com.bookstore.ordermanagement.models.Book;
import com.bookstore.ordermanagement.models.OrderSummary;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ResponseEntity<Boolean> responseEntity1;

    @InjectMocks
    private OrderService orderService;

    @Captor
    private ArgumentCaptor<Order> orderCaptor;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getAllOrders_ReturnsListOfOrders() {
        // Arrange
        List<Order> expectedOrders = Arrays.asList(
                new Order(1, "John Doe", "123 Street", 100.0, Arrays.asList(new BookDetail())),
                new Order(2, "Jane Smith", "456 Avenue", 200.0, Arrays.asList(new BookDetail()))
        );
        when(orderRepository.findAll()).thenReturn(expectedOrders);

        // Act
        List<Order> actualOrders = orderService.getAllOrders();

        // Assert
        assertEquals(expectedOrders.size(), actualOrders.size());
        assertEquals(expectedOrders.get(0), actualOrders.get(0));
        assertEquals(expectedOrders.get(1), actualOrders.get(1));
    }

    @Test
    public void getOrderById_ExistingOrderId_ReturnsOrder() {
        // Arrange
        int orderId = 1;
        Order expectedOrder = new Order(orderId, "John Doe", "123 Street", 100.0, Arrays.asList(new BookDetail()));
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(expectedOrder));

        // Act
        Order actualOrder = orderService.getOrderById(orderId);

        // Assert
        assertNotNull(actualOrder);
        assertEquals(expectedOrder, actualOrder);
    }

    @Test
    public void getOrderById_NonexistentOrderId_ReturnsNull() {
        // Arrange
        int orderId = 1;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // Act
        Order actualOrder = orderService.getOrderById(orderId);

        // Assert
        assertNull(actualOrder);
    }

    @Test
    public void placeOrder_AllBooksInStock_CreatesOrderAndReturnsOrderSummary() throws IllegalArgumentException, IOException {
        // Arrange
        Order order = createSampleOrder(); // Create a sample order object
        List<BookDetail> orderedBooks = order.getBookDetails();
        double totalAmount = 20.0; // Sample total amount
        List<Book> books = createSampleBooks(); // Create sample books list

        // Mock the response from the RestTemplate exchange
        String url = "http://book-service/books/order";
        ResponseEntity<JsonNode> responseEntity = new ResponseEntity<>(createSampleJsonNode(), HttpStatus.OK);
        when(restTemplate.exchange(eq(url), eq(HttpMethod.PUT), any(HttpEntity.class), eq(JsonNode.class))).thenReturn(responseEntity);

        when(objectMapper.readTree(anyString())).thenReturn(createSampleJsonNode());
        when(objectMapper.convertValue(any(JsonNode.class), any(TypeReference.class))).thenReturn(books);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // Act
        OrderSummary result = orderService.placeOrder(order);

        // Assert
        assertNotNull(result);
        assertEquals(order.getId(), result.getId());
        assertEquals(order.getCustomerName(), result.getCustomerName());
        assertEquals(order.getAddress(), result.getAddress());
        assertEquals(totalAmount, result.getTotalAmount(), 0.01);
        assertEquals(books, result.getBooks());
    }

    @Test
    public void placeOrder_BookNotInStock_ThrowsIllegalArgumentException() throws IOException {
        // Arrange
        Order order = createSampleOrder(); // Create a sample order object
        List<BookDetail> orderedBooks = order.getBookDetails();

        // Mock the response from the RestTemplate exchange
        String url = "http://book-service/books/order";
        ResponseEntity<JsonNode> responseEntity = new ResponseEntity<>(createSampleJsonNode(), HttpStatus.BAD_REQUEST);
        when(restTemplate.exchange(eq(url), eq(HttpMethod.PUT), any(HttpEntity.class), eq(JsonNode.class))).thenReturn(responseEntity);

        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> orderService.placeOrder(order));
    }

    @Test
    public void deleteOrder_ExistingOrderId_DeletesOrderAndReturnsTrue() {
        // Arrange
        int orderId = 1;
        Order expectedOrder = new Order(orderId, "John Doe", "123 Street", 100.0, Arrays.asList(new BookDetail()));
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(expectedOrder));
        when(restTemplate.exchange(anyString(), eq(HttpMethod.PUT), any(HttpEntity.class), eq(boolean.class))).thenReturn(new ResponseEntity<>(true, HttpStatus.OK));

        // Act
        boolean result = orderService.deleteOrder(orderId);

        // Assert
        assertTrue(result);
        verify(orderRepository, times(1)).deleteById(orderId);
    }

    @Test
    public void deleteOrder_NonexistentOrderId_ReturnsFalse() {
        // Arrange
        int orderId = 1;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // Act
        boolean result = orderService.deleteOrder(orderId);

        // Assert
        assertFalse(result);
        verify(orderRepository, never()).deleteById(anyInt());
    }

    // Helper methods to create sample objects for testing

    private Order createSampleOrder() {
        Order order = new Order();
        order.setId(1);
        order.setCustomerName("test user");
        order.setAddress("dummt address");
        order.setTotalAmount(0);
        order.setBookDetails(Collections.singletonList(new BookDetail(1, 1, 2))); // Sample book detail
        return order;
    }

    private List<Book> createSampleBooks() {
        List<Book> books = new ArrayList<>();
        books.add(new Book(1, "Book 1", "Author 1", "Description 1", 10.0, 5));
        return books;
    }

    private JsonNode createSampleJsonNode() {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.createObjectNode(); // Create an empty JsonNode for simplicity
    }
}
