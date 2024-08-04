package com.example.demo.repository;

import com.example.demo.model.Order;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends MongoRepository<Order, String>, OrderRepositoryCustom {

    Page<Order> findAllByStatusContainingIgnoreCase(String status, Pageable pageable);

    Page<Order> findAll(Pageable pageable);

    long countByStatus(String status);

    @Query("{ 'createdAt' : { $gte: ?0, $lt: ?1 } }")
    Optional<Long> countOrdersByCreatedAtBetween(Date startDate, Date endDate);

}

