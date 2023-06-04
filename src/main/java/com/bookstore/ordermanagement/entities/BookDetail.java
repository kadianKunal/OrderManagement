package com.bookstore.ordermanagement.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int fid;
    private int bookId;
    private int orderedQuantity;
}
