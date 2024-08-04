package com.example.demo.service;

import com.example.demo.dto.request.OrderCreateRequest;
import com.example.demo.dto.request.OrderItemRequest;
import com.example.demo.dto.request.ProductCart;
import com.example.demo.dto.request.ProductCartInner;
import com.example.demo.dto.response.*;
import com.example.demo.enums.OrderStatusEnums;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.mapper.OrderMapper;
import com.example.demo.mapper.ProductMapper;
import com.example.demo.model.Order;
import com.example.demo.model.Product;
import com.example.demo.model.ProductItem;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class OrderService {

    OrderRepository orderRepository;

    OrderMapper orderMapper;

    ProductMapper productMapper;

    ProductRepository productRepository;

    ProductService productService;

    public OrderResponse getOrders(String page, String status) {
        int pageNumber = 1;
        if (page != null && !page.isEmpty()) {
            try {
                pageNumber = Integer.parseInt(page);
            } catch (NumberFormatException e) {
                pageNumber = 1; // Xử lý khi page không phải là số hợp lệ
            }
        }

        Sort sort = Sort.by(Sort.Direction.DESC, "updated_at");
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, 10, sort);

        Page<OrderSingleResponse> orderPage;
        if (status != null && !status.isEmpty()) {
            orderPage = orderRepository.findAllByStatusContainingIgnoreCase(status, pageRequest).map(orderMapper::toOrderSingleResponse);
        } else {
            orderPage = orderRepository.findAll(pageRequest).map(orderMapper::toOrderSingleResponse);
        }

        return OrderResponse.builder()
                .orders(orderPage.getContent())
                .totalPage(orderPage.getTotalPages())
                .build();
    }


    public GetOrderDetail getOrderDetails(String id) {
        var order = orderRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXIST));
        List<ProductItem> productItems = order.getProducts();
        List<ProductOrderItem> productOrderItems = new ArrayList<>();
        for (ProductItem p : productItems) {
            var product = productRepository.findById(p.getProduct()).get();
            ProductOrderItem productOrderItem = ProductOrderItem.builder()
                    .product(productMapper.toProductSingleOfOrderDetail(product))
                    .quantity(p.getQuantity())
                    .build();
            productOrderItems.add(productOrderItem);
        }
        GetOrderDetail getOrderDetail = orderMapper.toGetOrderDetail(order);
        getOrderDetail.setProducts(productOrderItems);
        return getOrderDetail;
    }

    public void createOrder(OrderCreateRequest requestOrder) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // Phân tích chuỗi JSON thành danh sách ProductCart
            List<ProductCart> productCartList = objectMapper.readValue(requestOrder.getProducts(), new TypeReference<List<ProductCart>>() {
            });
            System.out.println("Parsed ProductCart List: " + productCartList);

            List<ProductItem> products = new ArrayList<>();
            int totalPrice = 0;
            for (ProductCart r : productCartList) {
                var product = productRepository.findById(r.getProduct().get_id()).orElse(null);
                if (product == null) {
                    throw new Exception("Sản phẩm không tồn tại với ID: " + r.getProduct().get_id());
                }
                if (product.getAvailableQuantity() < r.getQuantity()) {
                    throw new Exception("Sản phẩm " + product.getName() + " đã được đặt bởi người khác hoặc không đủ số lượng trong kho!");
                }
                totalPrice += product.getPrice() * r.getQuantity();
                product.setQuantity(product.getQuantity() - r.getQuantity());
                product.setAvailableQuantity(product.getAvailableQuantity() - r.getQuantity());
                productRepository.save(product);

                var productItem = ProductItem.builder()
                        .product(r.getProduct().get_id())
                        .quantity(r.getQuantity())
                        .build();
                products.add(productItem);
            }

            var order = orderMapper.toOrder(requestOrder);
            order.setCreatedAt(new Date());
            order.setUpdatedAt(new Date());
            order.setProducts(products);
            order.setPrice(totalPrice);
            order.setStatus("chờ");
            order.setPreviewPrice(productService.formatPrice(totalPrice));


            orderRepository.insert(order);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void updateOrder(String id, String statusJson) {
        var order = orderRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXIST));
        System.out.println(order);

        JSONObject jsonObject = new JSONObject(statusJson);
        String status = jsonObject.getString("status");
        var oldStatus = order.getStatus();

        System.out.println(status);
        System.out.println(oldStatus);

        int oldStatusTh = valueOfPriority(oldStatus);
        System.out.println("oldStatusTh" + oldStatusTh);
        int newStatusTh = valueOfPriority(status);
        System.out.println("newStatusTh" + newStatusTh);

        if (oldStatusTh == 1 && oldStatusTh < newStatusTh) {
            throw new AppException(ErrorCode.CANNOT_CANCEL);
        }
        if (oldStatusTh < newStatusTh) {
            throw new AppException(ErrorCode.OLD_STATUS);
        }
        order.setStatus(status);
        orderRepository.save(order);
    }

    public static int valueOfPriority(String message) {
        Map<String, Integer> statusMap = Map.of(
                OrderStatusEnums.WAIT.getMessage(), 5,
                OrderStatusEnums.ON_DELIVERY.getMessage(), 4,
                OrderStatusEnums.DELIVERED.getMessage(), 3,
                OrderStatusEnums.RECEIVED.getMessage(), 2,
                OrderStatusEnums.CANCEL.getMessage(), 1
        );
        return statusMap.get(message);
    }

}
