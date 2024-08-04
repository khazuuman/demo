package com.example.demo.repository;

import com.example.demo.model.Branch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BranchRepository extends MongoRepository<Branch, String> {

    Page<Branch> findAllBy(Pageable pageable);

    Page<Branch> findAllBySlugContainingIgnoreCase(String name, Pageable pageable);

}
