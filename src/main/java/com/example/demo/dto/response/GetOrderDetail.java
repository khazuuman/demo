package com.example.demo.dto.response;

import com.example.demo.model.Product;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class GetOrderDetail {

    String _id;
    String status;
    String name;
    String phone;
    String message;
    String address;
    int price;
    String previewPrice;
    List<Product> products;
    Date created_at;
    Date updated_at;

}
