package com.example.demo.mapper;

import com.example.demo.dto.response.BannerResponse;
import com.example.demo.model.Banner;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BannerMapper {


    @Mapping(target = "_id", source = "id")
    BannerResponse toBannerResponse (Banner banner);

}
