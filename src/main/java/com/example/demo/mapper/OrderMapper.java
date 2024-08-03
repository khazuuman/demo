package com.example.demo.mapper;

import com.example.demo.dto.request.OrderCreateRequest;
import com.example.demo.dto.response.GetOrderDetail;
import com.example.demo.dto.response.OrderResponse;
import com.example.demo.dto.response.OrderResponseDetail;
import com.example.demo.dto.response.OrderSingleResponse;
import com.example.demo.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderResponseDetail toOrderResponseDetail(Order order);

    @Mapping(target = "products", ignore = true)
    Order toOrder(OrderCreateRequest request);

    @Mapping(target = "_id", source = "id")
    OrderSingleResponse toOrderSingleResponse (Order order);

    @Mapping(target = "_id", source = "id")
    @Mapping(target = "products", ignore = true)
    GetOrderDetail toGetOrderDetail(Order order);

}
