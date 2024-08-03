package com.example.demo.dto.response;

import com.example.demo.model.Order;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class OrderResponse {

    List<OrderSingleResponse> orders;
    int totalPage;

}
