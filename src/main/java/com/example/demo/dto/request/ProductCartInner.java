package com.example.demo.dto.request;

import com.example.demo.model.Image;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.bson.types.ObjectId;
import org.springframework.lang.Nullable;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class ProductCartInner {
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
    String area;
    String category;
    List<Image> images;
    String status;
    Date created_at;
    Date updated_at;
}
