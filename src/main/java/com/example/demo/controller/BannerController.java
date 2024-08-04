package com.example.demo.controller;

import com.example.demo.dto.request.BannerUpdateRequest;
import com.example.demo.dto.response.BannerResponse;
import com.example.demo.exception.Errors;
import com.example.demo.model.Banner;
import com.example.demo.service.BannerService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

@RestController
@RequestMapping("/v1/api/banner")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class BannerController {

    BannerService bannerService;

    @GetMapping
    public List<BannerResponse> getBanners() throws IOException {
        return bannerService.getBanners();
    }

    @PostMapping
    public Errors createBanner(
            @RequestParam MultipartFile image,
            @RequestParam String url
    ) throws IOException {
        bannerService.createBanner(image, url);
        return Errors.builder()
                .message("Tạo banner thành công")
                .build();
    }

    @PutMapping("{id}")
    public Errors updateBanner(
            @PathVariable("id") String id,
            @RequestParam String url,
            @RequestParam String oldImageUrl,
            @RequestParam(required = false) MultipartFile image) throws IOException {
        bannerService.updateBanner(id, url, oldImageUrl, image);
        return Errors.builder()
                .message("Cập nhật banner thành công")
                .build();
    }

    @DeleteMapping("{id}")
    public Errors deleteBanner(@PathVariable("id") String id) throws IOException {
        bannerService.deleteBanner(id);
        return Errors.builder()
                .message("Xóa banner thành công!")
                .build();
    }

}
