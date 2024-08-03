package com.example.demo.service;

import com.example.demo.dto.request.AreaRequest;
import com.example.demo.dto.response.AreaResponse;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.mapper.AreaMapper;
import com.example.demo.model.Area;
import com.example.demo.repository.AreaRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AreaService {

    AreaRepository areaRepository;

    AreaMapper areaMapper;

    public List<AreaResponse> getAreas() {
        return areaRepository.findAll().stream().map(areaMapper::toAreaResponse).toList();
    }

    public void createArea(AreaRequest request) {
        if (areaRepository.findByName(request.getName()).isPresent())
            throw new AppException(ErrorCode.AREA_EXISTED);
        var area = Area.builder()
                .created_at(new Date())
                .updated_at(new Date())
                .name(request.getName())
                .slug(formatSlug(request.getName()))
                .build();
        areaRepository.insert(area);
    }

    public void updateArea(String id, AreaRequest request) {
        var area = areaRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.AREA_NOT_EXISTED));

        if (areaRepository.findByNameAndIdNot(request.getName(), id).isPresent())
            throw new AppException(ErrorCode.AREA_EXISTED);

        area.setUpdated_at(new Date());
        area.setName(request.getName());
        area.setSlug(formatSlug(request.getName()));

        areaRepository.save(area);
    }

    public void deleteArea(String id) {
        if (areaRepository.findById(id).isEmpty())
            throw new AppException(ErrorCode.AREA_NOT_EXISTED);
        areaRepository.deleteById(id);
    }

    public String formatSlug(String name) {
        String format = Normalizer.normalize(name.toLowerCase().replace(" ", "-"), Normalizer.Form.NFD);
        return format.replaceAll("\\p{M}", "");
    }

}
