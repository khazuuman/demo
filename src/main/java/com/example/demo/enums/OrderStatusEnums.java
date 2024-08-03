package com.example.demo.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum OrderStatusEnums {

    WAIT("chờ", 5),
    ON_DELIVERY("đang giao hàng", 4),
    DELIVERED("đã giao hàng", 3),
    RECEIVED("đã nhận hàng", 2),
    CANCEL("hủy", 1);

    String message;
    int priority;

}
