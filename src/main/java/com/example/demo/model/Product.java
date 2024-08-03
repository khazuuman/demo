package com.example.demo.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.lang.Nullable;

import java.util.Date;
import java.util.List;

@Document(value = "products")
@Data
@RequiredArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class Product {

    @Id
    String id;
    String name;
    String slug;
    String manufacture;
    String ingredient;
    List<String> uses;
    List<String> manual;
    String warning;
    List<String> storage_instructions;
    String expiry;
    int price;
    String preview_price;
    int availableQuantity;
    int quantity;
    @Nullable
    ObjectId area;
    @Nullable
    ObjectId category;
    List<Image> images;
    String status;
    Date created_at;
    Date updated_at;

}
