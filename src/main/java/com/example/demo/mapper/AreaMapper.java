package com.example.demo.mapper;

import com.example.demo.dto.response.AreaResponse;
import com.example.demo.model.Area;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Optional;

@Mapper(componentModel = "spring")
public interface AreaMapper {

    @Mapping(target = "_id", source = "id")
    AreaResponse toAreaResponse(Area area);

}
