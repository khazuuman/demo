package com.example.demo.dto.response;

import com.example.demo.model.Category;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class BestSellingProducts {

    List<Category> _id;
    long total;

}
