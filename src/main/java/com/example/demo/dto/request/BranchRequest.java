package com.example.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class BranchRequest {

    @NotBlank(message = "MANAGER_BLANK")
    String manager;
    @NotBlank(message = "PHONE_BLANK")
    @Pattern(regexp = "^\\d{10}$", message = "PHONE_INVALID")
    String phone;
    @NotBlank(message = "PROVINCE_BLANK")
    String province;
    String district;
    String address_details;
    String facebook;

}
