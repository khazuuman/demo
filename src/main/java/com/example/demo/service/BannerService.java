package com.example.demo.service;

import com.example.demo.dto.request.BannerUpdateRequest;
import com.example.demo.dto.response.BannerResponse;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.mapper.BannerMapper;
import com.example.demo.model.Banner;
import com.example.demo.model.Image;
import com.example.demo.repository.BannerRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Component
public class BannerService {

    BannerRepository bannerRepository;

    BannerMapper bannerMapper;

    public static String uploadDirectory = System.getProperty("user.dir") + "/src/main/resources/images";

    public List<BannerResponse> getBanners() throws IOException {
        return bannerRepository.findAll().stream().map(bannerMapper::toBannerResponse).toList();
    }

    public void createBanner(MultipartFile file, String url) throws IOException {

        if (file.isEmpty())
            throw new AppException(ErrorCode.FILE_BLANK);

        UploadImage image = uploadImage(file);

        var banner = Banner.builder()
                .created_at(new Date())
                .updated_at(new Date())
                .url(url)
                .image(new Image(image.getOriginalFileName(), image.getRelativePath()))
                .build();
        bannerRepository.insert(banner);
    }

    public void updateBanner(String id, String url, String oldImageUrl, MultipartFile file) throws IOException {
        var banner = bannerRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.BANNER_NOT_EXIST));
        String bannerName = banner.getImage().getName();
        banner.setUpdated_at(new Date());
        banner.setUrl(url);
        if (file != null || !file.isEmpty()) {
            log.warn(oldImageUrl);
            if (oldImageUrl != null && !oldImageUrl.isEmpty()) {
                deleteFile(bannerName);
            }
            UploadImage uploadImage = uploadImage(file);
            banner.setImage(new Image(uploadImage.getOriginalFileName(), uploadImage.getRelativePath()));
        }
        bannerRepository.save(banner);
    }

    public void deleteBanner(String id) throws IOException {
        var banner = bannerRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.BANNER_NOT_EXIST));
        deleteFile(banner.getImage().getName());
        bannerRepository.deleteById(id);
    }

    public String getRelativeFilePath(String filePath) {
        String keyword = "images";
        String normalizedFilePath = filePath.replace("\\", "/");
        int index = normalizedFilePath.indexOf(keyword);

        if (index != -1) {
            return normalizedFilePath.substring(index);
        }

        return "";
    }

    public void deleteFile(String originName) throws IOException {
        String absolutePath = Paths.get(uploadDirectory, originName).toString();
        File file = new File(absolutePath);

        if (file.exists()) {
            if (file.delete()) {
                log.info("Deleted file: " + absolutePath);
            } else {
                log.error("Failed to delete file: " + absolutePath);
            }
        } else {
            log.error("File does not exist: " + absolutePath);
        }
    }

    public UploadImage uploadImage(MultipartFile file) throws IOException {

        String originFileName = System.currentTimeMillis() + "-" + file.getOriginalFilename();
        String filePath = Paths.get(uploadDirectory, originFileName).toString();

        log.info(originFileName);
        log.info(filePath);

        String relativeFilePath = getRelativeFilePath(filePath);

        log.info(relativeFilePath);

        BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(filePath)));
        stream.write(file.getBytes());
        stream.close();

        return UploadImage.builder()
                .originalFileName(originFileName)
                .relativePath(relativeFilePath)
                .build();
    }
}
