package com.example.demo.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Date;

@Document(value = "branches")
@Data
@RequiredArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class Branch {

    @Id
    String id;
    String manager;
    String slug;
    String phone;
    String province;
    String provincePreview;
    String district;
    String address_details;
    String facebook;
    Date created_at;
    Date updated_at;

}
