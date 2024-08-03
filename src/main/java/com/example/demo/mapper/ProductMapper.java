package com.example.demo.mapper;

import com.example.demo.dto.request.ProductRequest;
import com.example.demo.dto.request.ProductUpdateRequest;
import com.example.demo.dto.response.ProductResponse;
import com.example.demo.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "_id", source = "id")
    @Mapping(target = "area", ignore = true)
    @Mapping(target = "category", ignore = true)
    ProductResponse toProductResponse(Product product);
    Product toProduct(ProductRequest request);

    @Mapping(target = "availableQuantity", ignore = true)
    void update(@MappingTarget Product product, ProductUpdateRequest request);
}
