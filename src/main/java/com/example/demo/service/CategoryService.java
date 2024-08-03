package com.example.demo.service;

import com.example.demo.dto.request.CategoryRequest;
import com.example.demo.dto.response.CategoryResponse;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.mapper.CategoryMapper;
import com.example.demo.model.Category;
import com.example.demo.repository.CategoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CategoryService {

    CategoryRepository categoryRepository;

    AreaService areaService;

    CategoryMapper categoryMapper;

    public List<CategoryResponse> getCategories() {
        return categoryRepository.findAll().stream().map(categoryMapper::toCategoryResponse).toList();
    }

    public void createCategory(CategoryRequest request) {
        if (categoryRepository.findByName(request.getName()).isPresent())
            throw new AppException(ErrorCode.CATE_NAME_EXISTED);

        var cate = Category.builder()
                .created_at(new Date())
                .updated_at(new Date())
                .slug(areaService.formatSlug(request.getName()))
                .name(request.getName())
                .build();

        categoryRepository.insert(cate);
    }

    public void updateCategory(String id, CategoryRequest request) {
        var cate = categoryRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.CATE_NOT_EXIST));
        if (categoryRepository.findByNameAndIdNot(request.getName(), id).isPresent()) {
            throw new AppException(ErrorCode.CATE_NAME_EXISTED);
        }
        cate.setName(request.getName());
        cate.setSlug(areaService.formatSlug(request.getName()));
        cate.setUpdated_at(new Date());

        categoryRepository.save(cate);
    }

    public void deleteCategory(String id) {
        if (!categoryRepository.existsById(id))
            throw new AppException(ErrorCode.CATE_NOT_EXIST);
        categoryRepository.deleteById(id);
    }

}
