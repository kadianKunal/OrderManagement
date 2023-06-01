package com.bookstore.ordermanagement.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Book {

    @JsonProperty(value = "id")
    private int id;

    @JsonProperty(value = "title")
    private String title;

    @JsonProperty(value = "author")
    private String author;

    @JsonProperty(value = "description")
    private String description;

    @JsonProperty(value = "price")
    private double price;

    @JsonProperty(value = "quantity")
    private int quantity;
}
