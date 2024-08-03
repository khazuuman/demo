package com.example.demo.dto.response;

import com.example.demo.model.Area;
import com.example.demo.model.Category;
import com.example.demo.model.Image;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class OrderProductDetail {

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
    String area;
    String category;
    List<Image> images;
    String status;
    Date created_at;
    Date updated_at;

}
