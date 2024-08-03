package com.example.demo.repository;

import com.example.demo.model.Area;
import com.example.demo.model.Category;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CategoryRepository extends MongoRepository<Category, String> {

    Optional<Category> findByName(String name);

    Optional<Category> findByNameAndIdNot(String name, String id);
}
