package com.example.demo.repository;

import com.example.demo.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderRepository extends MongoRepository<Order, String> {

    Page<Order> findAllByStatusContainingIgnoreCase(String status, Pageable pageable);

    Page<Order> findAll(Pageable pageable);
}
