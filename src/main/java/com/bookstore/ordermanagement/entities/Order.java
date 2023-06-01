package com.bookstore.ordermanagement.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String customerName;

    private String address;

    private double totalAmount;

    @OneToMany(cascade = CascadeType.ALL)
    private List<BookDetail> bookDetails;
}
