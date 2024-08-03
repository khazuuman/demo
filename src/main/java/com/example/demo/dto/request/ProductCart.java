package com.example.demo.dto.request;

import com.example.demo.dto.response.ProductResponse;
import com.example.demo.model.Image;
import com.example.demo.model.Product;
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
public class ProductCart {

   ProductCartInner product;
   int quantity;


}
