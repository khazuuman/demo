package com.example.demo.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class BranchSingleResponse {

    String _id;
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
