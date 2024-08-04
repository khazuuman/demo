package com.example.demo.controller;

import com.example.demo.dto.request.ProductCart;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.exception.Errors;
import com.example.demo.repository.ProductRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/v1/api")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class CartController {

    ProductRepository productRepository;
    ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/cart")
    public ResponseEntity<?> getCart(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return ResponseEntity.ok("Giỏ hàng rỗng");
        }

        Cookie cartCookie = findCookieByName(cookies, "cart");
        if (cartCookie == null) {
            return ResponseEntity.ok("Giỏ hàng rỗng");
        }

        String encodedCartJson = cartCookie.getValue();
        String cartJsonDecoded;
        try {
            cartJsonDecoded = new String(Base64.getDecoder().decode(encodedCartJson));
            List<ProductCart> cartItems = objectMapper.readValue(cartJsonDecoded, new TypeReference<>() {});
            return ResponseEntity.ok(cartItems);
        } catch (IOException e) {
            log.error("Lỗi xử lý dữ liệu trong cookie", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Lỗi xử lý dữ liệu trong cookie");
        }
    }

    @PostMapping("/add-to-cart")
    public ResponseEntity<Errors> addCart(@RequestBody ProductCart productCart,
                                          HttpServletRequest request,
                                          HttpServletResponse response) {
        try {
            var product = productRepository.findById(productCart.getProduct().get_id())
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
            if (productCart.getQuantity() <= 0) {
                return ResponseEntity.badRequest().body(Errors.builder().message("Số lượng không thể bằng 0 hoặc nhỏ hơn 0").build());
            }

            if (product.getAvailableQuantity() < productCart.getQuantity()) {
                return ResponseEntity.badRequest().body(Errors.builder().message("Số lượng thêm vào không thể nhiều hơn số lượng có sẵn!").build());
            }

            Cookie[] cookies = request.getCookies();
            Cookie cartCookie = findCookieByName(cookies, "cart");

            List<ProductCart> cartItems;
            if (cartCookie == null) {
                cartItems = new ArrayList<>();
                cartItems.add(productCart);
            } else {
                String encodedCartJson = cartCookie.getValue();
                String cartJsonDecoded = new String(Base64.getDecoder().decode(encodedCartJson));
                cartItems = objectMapper.readValue(cartJsonDecoded, new TypeReference<>() {});

                boolean productExists = false;
                for (ProductCart item : cartItems) {
                    if (item.getProduct().get_id().equals(productCart.getProduct().get_id())) {
                        item.setQuantity(item.getQuantity() + productCart.getQuantity());
                        productExists = true;
                        break;
                    }
                }

                if (!productExists) {
                    cartItems.add(productCart);
                }
            }

            String cartJson = objectMapper.writeValueAsString(cartItems);
            String encodedCartJson = Base64.getEncoder().encodeToString(cartJson.getBytes());

            if (cartCookie == null) {
                cartCookie = new Cookie("cart", encodedCartJson);
            } else {
                cartCookie.setValue(encodedCartJson);
            }
            cartCookie.setMaxAge(30 * 60); // Thời gian sống cookie 30 phút
            cartCookie.setPath("/"); // Đảm bảo cookie được gửi từ toàn bộ ứng dụng
            response.addCookie(cartCookie);

            return ResponseEntity.ok(Errors.builder().message("Đã thêm sản phẩm vào giỏ hàng!").build());
        } catch (JsonProcessingException e) {
            log.error("Lỗi xử lý dữ liệu JSON", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Errors.builder().message("Lỗi xử lý dữ liệu JSON").build());
        } catch (AppException e) {
            log.error("Lỗi sản phẩm không tìm thấy", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Errors.builder().message("Sản phẩm không tìm thấy").build());
        }
    }

    @DeleteMapping("/delete-to-cart/{id}")
    public ResponseEntity<Errors> deleteToCart(@PathVariable("id") String id,
                                               HttpServletRequest request,
                                               HttpServletResponse response) {
        try {
            Cookie[] cookies = request.getCookies();
            Cookie cartCookie = findCookieByName(cookies, "cart");

            if (cartCookie == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Errors.builder().message("Giỏ hàng không tồn tại").build());
            }

            String encodedCartJson = cartCookie.getValue();
            String cartJsonDecoded = new String(Base64.getDecoder().decode(encodedCartJson));

            List<ProductCart> cartItems = objectMapper.readValue(cartJsonDecoded, new TypeReference<>() {});
            System.out.println("list before delete " + cartItems.size());

            boolean removed = cartItems.removeIf(p -> p.getProduct().get_id().equals(id));
            System.out.println("list after delete " + cartItems.size());
            if (!removed) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Errors.builder().message("Sản phẩm không tìm thấy trong giỏ hàng").build());
            }

            String updatedCartJson = objectMapper.writeValueAsString(cartItems);
            String encodedUpdatedCartJson = Base64.getEncoder().encodeToString(updatedCartJson.getBytes());
            cartCookie.setValue(encodedUpdatedCartJson);
            cartCookie.setPath("/"); // Đảm bảo path của cookie chính xác
            cartCookie.setMaxAge(7 * 24 * 60 * 60); // Thiết lập thời hạn cookie là 7 ngày
            response.addCookie(cartCookie);

            return ResponseEntity.ok(Errors.builder().message("Đã xóa sản phẩm ra khỏi giỏ hàng!").build());
        } catch (IOException e) {
            log.error("Lỗi xử lý dữ liệu trong cookie", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Errors.builder().message("Lỗi xử lý dữ liệu trong cookie").build());
        }
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
