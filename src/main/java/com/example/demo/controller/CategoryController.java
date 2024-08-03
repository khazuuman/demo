package com.example.demo.controller;

import com.example.demo.dto.request.CategoryRequest;
import com.example.demo.dto.response.CategoryResponse;
import com.example.demo.exception.Errors;
import com.example.demo.model.Category;
import com.example.demo.service.CategoryService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/category")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class CategoryController {

    CategoryService categoryService;

    @GetMapping
    public List<CategoryResponse> getCategories() {
        return categoryService.getCategories();
    }

    @PostMapping
    public Errors createCategory(@RequestBody @Valid CategoryRequest request) {
        categoryService.createCategory(request);
        return Errors.builder()
                .message("Tạo danh mục thành công!")
                .build();
    }

    @PutMapping("{id}")
    public Errors updateCategory(@PathVariable("id") String id, @RequestBody @Valid CategoryRequest request) {
        categoryService.updateCategory(id, request);
        return Errors.builder()
                .message("Cập nhật danh mục thành công!")
                .build();
    }

    @DeleteMapping("{id}")
    public Errors deleteCategory(@PathVariable("id") String id) {
        categoryService.deleteCategory(id);
        return Errors.builder()
                .message("Xóa danh mục thành công!")
                .build();
    }
}
