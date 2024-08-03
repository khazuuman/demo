package com.example.demo.dto.request;

import com.example.demo.model.Product;
import com.example.demo.model.ProductItem;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class OrderCreateRequest {

    @NotBlank(message = "ORDER_NAME_BLANK")
    String name;
    @NotBlank(message = "PHONE_BLANK")
    @Pattern(regexp = "^\\d{10}$", message = "PHONE_INVALID")
    String phone;
    String message;
    @NotBlank(message = "ORDER_ADDRESS_BLANK")
    String address;
    String products;

}
