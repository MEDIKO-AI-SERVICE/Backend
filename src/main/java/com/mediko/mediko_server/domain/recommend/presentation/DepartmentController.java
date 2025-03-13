package com.mediko.mediko_server.domain.recommend.presentation;

import com.mediko.mediko_server.domain.recommend.application.DepartmentService;
import com.mediko.mediko_server.domain.recommend.domain.DepartmentTitle;
import com.mediko.mediko_server.domain.recommend.dto.response.DepartmentResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "department", description = "병원 진료과 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/department")
public class DepartmentController {

    private final DepartmentService departmentService;

    @Operation(summary = "전체 진료과 조회", description = "전체 진료과 이름과 설명을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<DepartmentResponseDTO>> getAllDepartments() {
        return ResponseEntity.ok(departmentService.getAllDepartments());
    }

    // 특정 진료과 정보 조회
    @Operation(summary = "특정 진료과 조회", description = "특정 진료과 이름에 따라 설명을 반환합니다.")
    @GetMapping("/{title}")
    public ResponseEntity<DepartmentResponseDTO> getDepartmentByTitle(
            @PathVariable("title") String title) {
        DepartmentTitle departmentTitle = departmentService.findDepartmentByTitle(title);
        String description = departmentService.getDepartmentDescription(title);
        return ResponseEntity.ok(new DepartmentResponseDTO(title, description));
    }
}
