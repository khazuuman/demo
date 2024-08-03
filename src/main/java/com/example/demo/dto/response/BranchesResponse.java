package com.example.demo.dto.response;

import com.example.demo.model.Branch;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class BranchesResponse {

    List<BranchSingleResponse> branches;
    int totalPage;

}
