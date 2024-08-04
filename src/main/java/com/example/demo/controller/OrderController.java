package com.example.demo.controller;

import com.example.demo.dto.request.OrderCreateRequest;
import com.example.demo.dto.response.GetOrderDetail;
import com.example.demo.dto.response.OrderResponse;
import com.example.demo.dto.response.OrderResponseDetail;
import com.example.demo.dto.response.OrderSingleResponse;
import com.example.demo.exception.Errors;
import com.example.demo.service.OrderService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
//@RequestMapping("/v1/api/order")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    OrderService orderService;

    @GetMapping("/v1/api/order")
    public OrderResponse getOrders(@RequestParam(required = false) String status, @RequestParam(required = false) String page) {
        return orderService.getOrders(page, status);
    }

    @GetMapping("/v1/api/order/{id}")
    public GetOrderDetail getOrderDetails(@PathVariable("id") String id) {
        return orderService.getOrderDetails(id);
    }

    @PostMapping("/v1/api/create-order")
    public Errors createOrder(@RequestBody OrderCreateRequest products, HttpServletRequest request, HttpServletResponse response) throws Exception {
        orderService.createOrder(products);

        Cookie[] cookies = request.getCookies();
        Cookie cartCookie = findCookieByName(cookies, "cart");

        if (cartCookie != null) {
            cartCookie.setMaxAge(0); // Đặt thời gian hết hạn của cookie thành 0 để xóa nó
            cartCookie.setValue(null); // Xóa giá trị của cookie
            cartCookie.setPath("/"); // Thiết lập lại đường dẫn cho cookie
            response.addCookie(cartCookie); // Thêm cookie vào phản hồi để xóa nó trên trình duyệt
        }

        return Errors.builder()
                .message("Tạo đơn hàng thành công!")
                .build();
    }

    @PutMapping("/v1/api/order/{id}")
    public Errors updateOrder(@PathVariable("id") String id,@RequestBody String status) {
        orderService.updateOrder(id, status);
        return Errors.builder()
                .message("Cập nhật trạng thái đơn hàng thành công")
                .build();
    }

    private Cookie findCookieByName(Cookie[] cookies, String name) {
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (name.equals(cookie.getName())) {
                    return cookie;
                }
            }
        }
        return null;
    }


}
