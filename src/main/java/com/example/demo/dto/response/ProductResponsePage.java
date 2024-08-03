package com.example.demo.dto.response;

import com.example.demo.model.Product;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class ProductResponsePage {

    List<ProductResponse> products;
    int totalPage;

}
