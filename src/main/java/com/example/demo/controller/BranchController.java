package com.example.demo.controller;

import com.example.demo.dto.request.BranchRequest;
import com.example.demo.dto.response.BranchesResponse;
import com.example.demo.exception.Errors;
import com.example.demo.service.BranchService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/branch")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class BranchController {

    BranchService branchService;

    @GetMapping
    public BranchesResponse getBranches(
            @RequestParam(defaultValue = "1") String page,
            @RequestParam(defaultValue = "") String sortColumn,
            @RequestParam(defaultValue = "") String sortDirection,
            @RequestParam(defaultValue = "") String search
    ) {
        return branchService.getBranches(page, sortColumn, sortDirection, search);
    }

    @PostMapping
    public Errors createBranch(@RequestBody @Valid BranchRequest request) {
        branchService.createBranch(request);
        return Errors.builder()
                .message("Tạo chi nhánh thành công!")
                .build();
    }

    @PutMapping("{id}")
    public Errors updateBranch(@PathVariable("id") String id, @RequestBody @Valid BranchRequest request) {
        branchService.updateBranch(id, request);
        return Errors.builder()
                .message("Cập nhật chi nhánh thành công!")
                .build();
    }

    @DeleteMapping("{id}")
    public Errors deleteBranch(@PathVariable("id") String id) {
        branchService.deleteBranch(id);
        return Errors.builder()
                .message("Xóa khu vực thành công!")
                .build();
    }
}
