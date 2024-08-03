package com.example.demo.service;

import com.example.demo.dto.request.BranchRequest;
import com.example.demo.dto.response.BranchSingleResponse;
import com.example.demo.dto.response.BranchesResponse;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.model.Branch;
import com.example.demo.repository.BranchMapper;
import com.example.demo.repository.BranchRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class BranchService {

    BranchRepository branchRepository;

    AreaService areaService;

    BranchMapper branchMapper;
    public BranchesResponse getBranches(String page, String sortColumn, String sortDirection, String search) {
        int pageNumber = 0;
        if (!page.isEmpty()) {
            pageNumber = Integer.parseInt(page);
        }

        Sort sort;
        if (sortDirection.equals("1")) {
            sort = Sort.by(Sort.Direction.ASC, sortColumn);
        } else if (sortDirection.equals("-1")) {
            sort = Sort.by(Sort.Direction.DESC, sortColumn);
        } else {
            sort = Sort.unsorted();
        }

        PageRequest pageRequest = PageRequest.of(pageNumber - 1, 10, sort);

        // Lấy dữ liệu với phân trang và sắp xếp
        Page<Branch> branchPage = branchRepository.findAllByManagerContainingIgnoreCase(search, pageRequest);

        // Chuyển đổi Page<Branch> thành List<BranchResponse>
        List<BranchSingleResponse> branchResponses = branchPage.getContent().stream()
                .map(branchMapper::toBranchSingleResponse)
                .collect(Collectors.toList());

        // Tính tổng số trang
        int totalElements = (int) branchPage.getTotalElements();
        int totalPage = (int) Math.ceil((double) totalElements / 10);

        return BranchesResponse.builder()
                .branches(branchResponses)
                .totalPage(totalPage)
                .build();
    }

    public void createBranch(BranchRequest request) {
        Branch branch = Branch.builder()
                .manager(request.getManager())
                .slug(areaService.formatSlug(request.getManager()))
                .phone(request.getPhone())
                .province(request.getProvince())
                .provincePreview(areaService.formatSlug(request.getProvince()))
                .district(request.getDistrict())
                .address_details(request.getAddress_details())
                .facebook(request.getFacebook())
                .created_at(new Date())
                .updated_at(new Date())
                .build();
        branchRepository.insert(branch);
    }

    public void updateBranch(String id, BranchRequest request) {
        var branch = branchRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.BRANCH_NOT_EXIST));
        branch.setManager(request.getManager());
        branch.setPhone(request.getPhone());
        branch.setProvince(request.getProvince());
        branch.setDistrict(request.getDistrict());
        branch.setAddress_details(request.getAddress_details());
        branch.setFacebook(request.getFacebook());
        branch.setUpdated_at(new Date());
        branch.setSlug(areaService.formatSlug(request.getManager()));
        branch.setProvincePreview(areaService.formatSlug(request.getProvince()));

        branchRepository.save(branch);
    }

    public void deleteBranch(String id) {
        if (branchRepository.findById(id).isEmpty())
            throw new AppException(ErrorCode.BRANCH_NOT_EXIST);
        branchRepository.deleteById(id);
    }


}
