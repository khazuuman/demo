package com.example.demo.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class AreaResponse {

    String _id;
    String name;
    String slug;
    Date created_at;
    Date updated_at;

}
