package com.mediko.mediko_server.domain.recommend.application;

import com.mediko.mediko_server.domain.recommend.domain.DepartmentDescription;
import com.mediko.mediko_server.domain.recommend.domain.DepartmentTitle;
import com.mediko.mediko_server.domain.recommend.dto.response.DepartmentResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    // 모든 진료과 목록 조회
    public List<DepartmentResponseDTO> getAllDepartments() {
        return Arrays.stream(DepartmentTitle.values())
                .map(title -> new DepartmentResponseDTO(
                        title.getValue(),
                        DepartmentDescription.valueOf(title.name()).getValue()
                ))
                .collect(Collectors.toList());
    }

    // 진료과 title로 Enum 찾기
    public DepartmentTitle findDepartmentByTitle(String title) {
        return Arrays.stream(DepartmentTitle.values())
                .filter(t -> t.getValue().equals(title))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid department title: " + title));
    }

    // 진료과 설명 조회
    public String getDepartmentDescription(String title) {
        DepartmentTitle departmentTitle = findDepartmentByTitle(title);
        return DepartmentDescription.valueOf(departmentTitle.name()).getValue();
    }
}
