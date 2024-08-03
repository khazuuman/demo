package com.example.demo.dto.request;

import com.example.demo.validator.StatusValidate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.bson.types.ObjectId;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class ProductRequest {

    @NotBlank(message = "PRODUCT_NAME_BLANK")
    String name;
    @NotBlank(message = "MANUFACTURE_NAME_BLANK")
    String manufacture;
    @NotBlank(message = "INGREDIENT_NAME_BLANK")
    String ingredient;
    List<String> uses;
    List<String> manual;
    String warning;
    List<String> storage_instructions;
    @NotBlank(message = "EXPIRY_NAME_BLANK")
    String expiry;
    @Positive(message = "PRICE_INVALID")
    int price;
    @Positive(message = "QUANTITY_INVALID")
    int quantity;
    @Nullable
    ObjectId area;
    @Nullable
    ObjectId category;
    @NotBlank(message = "STATUS_BLANK")
    @StatusValidate(message = "STATUS_INVALID")
    String status;

}
