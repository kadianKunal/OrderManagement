package com.bookstore.ordermanagement.dao;

import com.bookstore.ordermanagement.entities.Order;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<Order, Integer> {

}
