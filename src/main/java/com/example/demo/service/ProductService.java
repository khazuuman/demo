package com.example.demo.service;

import com.example.demo.dto.request.ProductRequest;
import com.example.demo.dto.request.ProductUpdateRequest;
import com.example.demo.dto.response.ProductResponse;
import com.example.demo.dto.response.ProductResponsePage;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.mapper.ProductMapper;
import com.example.demo.model.Image;
import com.example.demo.model.Product;
import com.example.demo.repository.AreaRepository;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ProductRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ProductService {

    ProductRepository productRepository;

    MongoTemplate mongoTemplate;

    ProductMapper productMapper;

    AreaRepository areaRepository;

    CategoryRepository categoryRepository;

    AreaService areaService;

    BannerService bannerService;

    public ProductResponsePage getProducts(String page, String search, String area, String category, String status) throws IOException {
        int pageNumber = 0;
        if (!page.isEmpty())
            pageNumber = Integer.parseInt(page) - 1;

        PageRequest pageRequest = PageRequest.of(pageNumber, 8);

        Criteria criteria = new Criteria();
        if (search != null && !search.isEmpty()) {
            criteria.and("name").regex(search, "i");
        }
        if (area != null && !area.isEmpty()) {
            try {
                ObjectId areaId = new ObjectId(area);
                criteria.and("area").is(areaId);
            } catch (IllegalArgumentException e) {
                e.getMessage();
            }
        }
        if (category != null && !category.isEmpty()) {
            try {
                ObjectId categoryId = new ObjectId(category);
                criteria.and("category").is(categoryId);
            } catch (IllegalArgumentException e) {
                e.getMessage();
            }
        }
        if (status != null && !status.isEmpty()) {
            criteria.and("status").is(status);
        }

        Query query = new Query(criteria).with(pageRequest);
        List<Product> products = mongoTemplate.find(query, Product.class);

        long total = mongoTemplate.count(query.skip(-1).limit(-1), Product.class);

        List<ProductResponse> productResponses = new ArrayList<>();
        for (Product p : products) {
            var productResponse = productMapper.toProductResponse(p);
            var areaObject = areaRepository.findById(p.getArea().toString()).orElse(null);
            var cateObject = categoryRepository.findById(p.getCategory().toString()).orElse(null);
            productResponse.setArea(Optional.ofNullable(areaObject));
            productResponse.setCategory(Optional.ofNullable(cateObject));
            productResponses.add(productResponse);

        }

        return ProductResponsePage.builder()
                .products(productResponses)
                .totalPage((int) Math.ceil((double) total / pageRequest.getPageSize()))
                .build();
    }

    public ProductResponse getProductDetails(String id) {
        var product = productRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        return productMapper.toProductResponse(product);
    }

    public void createProduct(ProductRequest request, List<MultipartFile> images) throws IOException {

        if (images.size() == 1) {
            if (images.get(0).getOriginalFilename().isEmpty())
                throw new AppException(ErrorCode.IMAGES_BLANK);
        }
        if (images.size() > 5) {
            throw new AppException(ErrorCode.IMAGES_EXCEED);
        }

        if (productRepository.findByName(request.getName()).isPresent())
            throw new AppException(ErrorCode.PRODUCT_EXISTED);

        Product product = new Product();

        product.setName(request.getName());
        product.setManufacture(request.getManufacture());
        product.setIngredient(request.getIngredient());
        product.setWarning(request.getWarning());
        product.setExpiry(request.getExpiry());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());
        product.setStatus(request.getStatus());
        product.setCreated_at(new Date());
        product.setUpdated_at(new Date());

        product.setArea(request.getArea() != null ? request.getArea() : new ObjectId());
        product.setCategory(request.getCategory() != null ? request.getCategory() : new ObjectId());
        if (request.getUses() != null && !request.getManual().isEmpty()) {
            product.setUses(request.getUses());
        }
        product.setManual(request.getManual() != null ? request.getManual() : new ArrayList<>());
        product.setStorage_instructions(request.getStorage_instructions() != null ? request.getStorage_instructions() : new ArrayList<>());
        product.setSlug(areaService.formatSlug(request.getName()));
        product.setPreview_price(formatPrice(product.getPrice()));
        product.setAvailableQuantity(request.getQuantity());

        List<Image> imagesProduct = new ArrayList<>();
        for (MultipartFile i : images) {
            UploadImage uploadImage = bannerService.uploadImage(i);
            imagesProduct.add(new Image(uploadImage.getOriginalFileName(), uploadImage.getRelativePath()));
        }
        product.setImages(imagesProduct);
        productRepository.save(product);
    }

    public void updateProduct(String id, ProductUpdateRequest request, List<MultipartFile> newList) throws IOException {
        var product = productRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        int errorTerm = request.getQuantity() - product.getQuantity();
        int availableQuantity = request.getAvailableQuantity() - errorTerm;
        if (availableQuantity <= 0) {
            throw new AppException(ErrorCode.AVAILABLE_QUANTITY);
        }

        productMapper.update(product, request);

        if (newList != null) {
            if (newList.size() == 1) {
                if (Objects.requireNonNull(newList.get(0).getOriginalFilename()).isEmpty())
                    throw new AppException(ErrorCode.IMAGES_BLANK);
            }
            if (newList.size() > 5) {
                throw new AppException(ErrorCode.IMAGES_EXCEED);
            }
            List<Image> oldImages = product.getImages();

            List<String> oldImageName = new ArrayList<>();
            for (Image i : oldImages) {
                oldImageName.add(i.getName());
            }
            List<String> newImageName = new ArrayList<>();
            assert newList != null;
            for (MultipartFile n : newList) {
                newImageName.add(n.getOriginalFilename());
            }

//        add image to folder
            List<String> addImageName = new ArrayList<>(newImageName);
            addImageName.removeAll(oldImageName);

            List<MultipartFile> addFile = new ArrayList<>();
            for (MultipartFile n : newList) {
                for (String s : addImageName) {
                    if (Objects.equals(n.getOriginalFilename(), s)) {
                        addFile.add(n);
                    }
                }
            }

            List<Image> newImages = new ArrayList<>();
            for (MultipartFile a : addFile) {
                UploadImage uploadImage = bannerService.uploadImage(a);
                newImages.add(new Image(uploadImage.getOriginalFileName(), uploadImage.getRelativePath()));
            }

//      delete image
//      list nhung image name can xoa
            List<String> removeImageName = new ArrayList<>(oldImageName);
            oldImageName.removeAll(newImageName);

            for (Image i : oldImages) {
                for (String s : removeImageName) {
                    if (Objects.equals(i.getName(), s))
                        bannerService.deleteFile(i.getName());
                }
            }

            List<Image> sharedList = new ArrayList<>(oldImages);
            sharedList.retainAll(newImages);
            product.setImages(sharedList);
        }

        product.setAvailableQuantity(request.getAvailableQuantity());
        product.setUpdated_at(new Date());
        productRepository.save(product);
    }

    public void deleteProduct(String id) throws IOException {
        var product = productRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        List<Image> images = product.getImages();
        if (!images.isEmpty()) {
            for (Image i : images) {
                bannerService.deleteFile(i.getName());
            }
        }
        productRepository.deleteById(id);

    }

    public String formatPrice(int price) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setGroupingSeparator('.');
        DecimalFormat decimalFormat = new DecimalFormat("#,###", symbols);
        return decimalFormat.format(price) + " ₫";
    }

}