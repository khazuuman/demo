package com.example.demo.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Document(value = "orders")
@Data
@RequiredArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Order {

    @Id
    String id;
    String status;
    String name;
    @NotBlank(message = "PHONE_BLANK")
    @Pattern(regexp = "^\\d{10}$", message = "PHONE_INVALID")
    String phone;
    String message;
    String address;
    int price;
    String previewPrice;
    List<ProductItem> products;
    @Field(name = "created_at")
    Date createdAt;
    @Field(name = "updated_at")
    Date updatedAt;

}
