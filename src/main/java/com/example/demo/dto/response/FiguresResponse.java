package com.example.demo.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class FiguresResponse {

    Long totalOrders;
    Long pendingOrders;
    Long deliveringOrders;
    Long deliveredOrders;
    Long receivedOrders;
    Long cancelOrders;
    Long todayOrders;
    Long yesterdayOrders;
    Long thisMonthOrders;
    Long lastMonthOrders;
    List<WeeklyOrders> weeklyOrders;
    List<BestSellingProducts> bestSellingProducts;


}
