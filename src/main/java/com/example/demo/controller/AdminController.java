package com.example.demo.controller;

import com.example.demo.dto.request.AdminLoginRequest;
import com.example.demo.dto.response.TokenResponse;
import com.example.demo.exception.Errors;
import com.example.demo.model.Admin;
import com.example.demo.service.AdminService;
import com.nimbusds.jose.JOSEException;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/v1/api")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    AdminService adminService;

    @GetMapping("/get_admin")
    public Admin getAdmin() {
        return adminService.getAdmin();
    }

    @PostMapping("/login")
    public TokenResponse login(@RequestBody @Valid AdminLoginRequest adminLoginRequest) {
        return adminService.login(adminLoginRequest);
    }

    @PostMapping("/logout")
    public Errors logout() throws ParseException, JOSEException {
        adminService.logout();
        return Errors.builder()
                .message("Đăng xuất thành công!")
                .build();
    }

    @PostMapping("/change_password")
    public Errors changePassword(@RequestParam String newPassword, @RequestParam String oldPassword) throws ParseException, JOSEException {
        adminService.changePassword(newPassword, oldPassword);
        return Errors.builder()
                .message("Cập nhật mật khẩu thành công")
                .build();
    }

}
