package com.bookstore.ordermanagement.entities;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
public class BookDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int fid;
    private int bookId;
    private int orderedQuantity;
}
