package com.example.demo.repository;

import com.example.demo.dto.response.WeeklyOrders;

import java.util.List;

public interface OrderRepositoryCustom {
    List<WeeklyOrders> findOrdersForLast7Days();
}
