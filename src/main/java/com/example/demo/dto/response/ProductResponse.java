package com.example.demo.dto.response;

import com.example.demo.model.Area;
import com.example.demo.model.Category;
import com.example.demo.model.Image;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.bson.types.ObjectId;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class ProductResponse {

    String _id;
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
    Optional<Area> area;
    Optional<Category> category;
    List<Image> images;
    String status;
    Date created_at;
    Date updated_at;

}
