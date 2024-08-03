package com.example.demo.repository;

import com.example.demo.model.InvalidToken;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface InvalidTokenRepository extends MongoRepository<InvalidToken, String> {

}
