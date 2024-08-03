package com.example.demo.mapper;

import com.example.demo.dto.response.CategoryResponse;
import com.example.demo.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "_id", source = "id")
    CategoryResponse toCategoryResponse (Category category);

}
