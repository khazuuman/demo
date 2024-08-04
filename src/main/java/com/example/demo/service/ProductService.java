package com.example.demo.service;

import com.example.demo.dto.request.ProductRequest;
import com.example.demo.dto.request.ProductUpdateRequest;
import com.example.demo.dto.request.UpdateImageProduct;
import com.example.demo.dto.response.ProductResponse;
import com.example.demo.dto.response.ProductResponsePage;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.mapper.ProductMapper;
import com.example.demo.model.Area;
import com.example.demo.model.Category;
import com.example.demo.model.Image;
import com.example.demo.model.Product;
import com.example.demo.repository.AreaRepository;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ProductRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.stream.Collectors;

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
        if (!page.isEmpty()) {
            pageNumber = Integer.parseInt(page) - 1;
        }

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
                e.getMessage(); // Log or handle the exception as needed
            }
        }
        if (category != null && !category.isEmpty()) {
            try {
                ObjectId categoryId = new ObjectId(category);
                criteria.and("category").is(categoryId);
            } catch (IllegalArgumentException e) {
                e.getMessage(); // Log or handle the exception as needed
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
            ProductResponse productResponse = productMapper.toProductResponse(p);

            if (p.getArea() == null) {
                productResponse.setArea(null);
            } else {
                Optional<Area> areaObject = areaRepository.findById(p.getArea().toString());
                productResponse.setArea(areaObject);
            }

            if (p.getCategory() == null) {
                productResponse.setCategory(null);
            } else {
                Optional<Category> cateObject = categoryRepository.findById(p.getCategory().toString());
                productResponse.setCategory(cateObject);
            }

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

        product.setArea(request.getArea() != null && !request.getArea().equals("") ? new ObjectId(request.getArea()) : null);
        product.setCategory(request.getCategory() != null && !request.getCategory().equals("") ? new ObjectId(request.getCategory()) : null);

        String usesJson = request.getUses();
        String manualJson = request.getManual();
        String storageInstructionsJson = request.getStorage_instructions();

        ObjectMapper objectMapper = new ObjectMapper();

        List<String> uses = objectMapper.readValue(usesJson, new TypeReference<List<String>>() {});
        List<String> manual = objectMapper.readValue(manualJson, new TypeReference<List<String>>() {});
        List<String> storageInstructions = objectMapper.readValue(storageInstructionsJson, new TypeReference<List<String>>() {});

        product.setUses(uses);
        product.setManual(manual);
        product.setStorage_instructions(storageInstructions);
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

    public void updateProduct(
            String id,
            ProductUpdateRequest request,
            List<MultipartFile> images,
            String oldImagesJson,
            String newImagesJson
    ) throws IOException {
        var product = productRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        // Check quantity
        int errorTerm = request.getQuantity() - product.getQuantity();
        int availableQuantity = product.getAvailableQuantity() - errorTerm;
        if (availableQuantity <= 0) {
            throw new AppException(ErrorCode.AVAILABLE_QUANTITY);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        List<UpdateImageProduct> oldImages = objectMapper.readValue(oldImagesJson, new TypeReference<List<UpdateImageProduct>>() {
        });
        List<UpdateImageProduct> newImages = objectMapper.readValue(newImagesJson, new TypeReference<List<UpdateImageProduct>>() {
        });

        List<UpdateImageProduct> deleteList = filterOldImages(oldImages, newImages);

        // Calculate the total number of new and existing images
        int exceed = (images != null ? images.size() : 0) + (newImages != null ? newImages.size() : 0);
        if (exceed > 5) {
            throw new AppException(ErrorCode.IMAGES_EXCEED);
        }
        if (exceed == 0) {
            throw new AppException(ErrorCode.IMAGES_EXCEED);
        }

        List<Image> imageListSaveDb = new ArrayList<>();

        // Add new files
        List<Image> newFileImages = new ArrayList<>();
        if (images != null && !images.isEmpty()) {
            for (MultipartFile f : images) {
                UploadImage image = bannerService.uploadImage(f);
                newFileImages.add(Image.builder()
                        .name(image.getOriginalFileName())
                        .url(image.getRelativePath())
                        .build());
            }
            imageListSaveDb.addAll(newFileImages);
        }

        // Handle deletion and updating of images
        if (deleteList != null && !deleteList.isEmpty()) {
            for (UpdateImageProduct u : deleteList) {
                bannerService.deleteFile(u.getName());
            }
        }

        // Map product
        List<Image> newImageInput = new ArrayList<>();
        for (UpdateImageProduct u : newImages) {
            newImageInput.add(Image.builder()
                    .name(u.getName())
                    .url(u.getUrl())
                    .build());
        }
        imageListSaveDb.addAll(newImageInput);
        productMapper.update(product, request);
        product.setUpdated_at(new Date());
        product.setImages(imageListSaveDb);
        productRepository.save(product);
    }

    public static List<Image> combineImages(List<Image> newFileImages, List<Image> currentImages) {
        Set<Image> combinedSet = new HashSet<>(newFileImages);
        combinedSet.addAll(currentImages);
        return new ArrayList<>(combinedSet);
    }

    public static List<UpdateImageProduct> filterOldImages(List<UpdateImageProduct> oldImages, List<UpdateImageProduct> newImages) {
        // Convert newImages to a Set of URLs for efficient lookup
        Set<String> newImageUrls = newImages.stream()
                .map(UpdateImageProduct::getUrl)
                .collect(Collectors.toSet());

        // Filter oldImages that are not in newImageUrls
        return oldImages.stream()
                .filter(oldImage -> !newImageUrls.contains(oldImage.getUrl()))
                .collect(Collectors.toList());
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