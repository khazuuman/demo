package com.example.demo.repository;

import com.example.demo.dto.response.BranchSingleResponse;
import com.example.demo.model.Branch;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BranchMapper {

    @Mapping(target = "_id", source = "id")
    BranchSingleResponse toBranchSingleResponse (Branch branch);

}
