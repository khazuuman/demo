package com.example.demo.controller;

import com.example.demo.dto.request.ProductRequest;
import com.example.demo.dto.request.ProductUpdateRequest;
import com.example.demo.dto.response.ProductResponse;
import com.example.demo.dto.response.ProductResponsePage;
import com.example.demo.exception.Errors;
import com.example.demo.service.ProductService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/v1/api/product")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    ProductService productService;

    @GetMapping
    public ProductResponsePage getProducts(
            @RequestParam(defaultValue = "1") String page,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "") String area,
            @RequestParam(defaultValue = "") String category,
            @RequestParam(defaultValue = "") String status
    ) throws IOException {
        return productService.getProducts(page, search, area, category, status);
    }

    @GetMapping("{id}")
    public ProductResponse getProductDetails(@PathVariable("id") String id) {
        return productService.getProductDetails(id);
    }

    @PostMapping
    public Errors createProduct(
            @ModelAttribute @Valid ProductRequest request,
            @RequestParam(required = false) List<MultipartFile> images
    ) throws IOException {
        productService.createProduct(request, images);
        return Errors.builder()
                .message("Tạo sản phẩm thành công!")
                .build();
    }

    @DeleteMapping("{id}")
    public Errors deleteProduct(@PathVariable("id") String id) throws IOException {
        productService.deleteProduct(id);
        return Errors.builder()
                .message("Xóa sản phẩm thành công!")
                .build();
    }

    @PutMapping("{id}")
    public Errors updateProduct(
            @PathVariable("id") String id,
            @RequestParam(required = false) List<MultipartFile> images,
            @ModelAttribute ProductUpdateRequest request
    ) throws IOException {
        productService.updateProduct(id, request, images);
        return Errors.builder()
                .message("Cập nhật sản phẩm thành công!")
                .build();
    }

}
