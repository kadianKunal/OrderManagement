package com.bookstore.ordermanagement.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderSummary {

    private int id;

    private String customerName;

    private String address;

    private double totalAmount;

    private List<Book> books;
}
