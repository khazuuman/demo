package com.example.demo.repository;

import com.example.demo.model.Banner;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface BannerRepository extends MongoRepository<Banner, String> {

    Optional<Banner> findByImageUrl(String url);

}
