package com.example.demo.repository;

import com.example.demo.model.Area;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface AreaRepository extends MongoRepository<Area, String> {

    Optional<Area> findByName(String name);

    Optional<Area> findByNameAndIdNot(String name, String id);

}
