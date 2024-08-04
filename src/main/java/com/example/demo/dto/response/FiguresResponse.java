package com.example.demo.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class FigureResponse {

    int totalOrders;
    int pendingOrders;
    int deliveringOrders;
    int deliveredOrders;
    int receivedOrders;
    int cancelOrders;
    int todayOrders;
    int yesterdayOrders;
    int thisMonthOrders;
    int lastMonthOrders;
    List<WeeklyOrders> weeklyOrders;
    List<BestSellingProducts> bestSellingProducts;


}
