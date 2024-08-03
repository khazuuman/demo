package com.example.demo.controller;

import com.example.demo.dto.request.AreaRequest;
import com.example.demo.dto.response.AreaResponse;
import com.example.demo.exception.Errors;
import com.example.demo.model.Area;
import com.example.demo.service.AreaService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/area")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class AreaController {

    AreaService areaService;

    @GetMapping
    public List<AreaResponse> getAreas() {
        return areaService.getAreas();
    }

    @PostMapping
    public Errors createAreas(@RequestBody @Valid AreaRequest request) {
        areaService.createArea(request);
        return Errors.builder()
                .message("Tạo khu vực thành công!")
                .build();
    }

    @PutMapping("{id}")
    public Errors updateAreas(@PathVariable String id, @RequestBody @Valid AreaRequest request) {
        areaService.updateArea(id, request);
        return Errors.builder()
                .message("Cập nhật khu vực thành công!")
                .build();
    }

    @DeleteMapping("{id}")
    public Errors deleteArea(@PathVariable String id) {
        areaService.deleteArea(id);
        return Errors.builder()
                .message("Xóa khu vực thành công!")
                .build();
    }
}
