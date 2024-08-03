package com.example.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class AdminLoginRequest {

    @NotBlank(message = "USERNAME_BLANK")
    String username;
    @NotBlank(message = "PASSWORD_BLANK")
    String password;

}
