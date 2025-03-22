package com.mediko.mediko_server.domain.report.presentation;

import com.mediko.mediko_server.domain.member.application.CustomUserDetails;
import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.report.application.ReportService;
import com.mediko.mediko_server.domain.report.dto.request.ReportRequestDTO;
import com.mediko.mediko_server.domain.report.dto.response.ReportResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "report", description = "AI 문진 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/report")
public class ReportController {
    private final ReportService reportService;

    @Operation(summary = "문진 생성", description = "AI로 응답받은 문진을 저장합니다.")
    @PostMapping
    public ResponseEntity<Map<String, Object>> generateReport(
            @RequestBody ReportRequestDTO reportRequestDTO,
            @AuthenticationPrincipal CustomUserDetails userDetail) {

        Member member = userDetail.getMember();
        Map<String, Object> response = reportService.generateReport(reportRequestDTO, member);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "문진 조회 (환자용)", description = "특정 문진을 환자용으로 조회합니다.")
    @GetMapping("/patient/{reportId}")
    public ResponseEntity<ReportResponseDTO> getPatientReport(
            @PathVariable("reportId") Long reportId,
            @AuthenticationPrincipal CustomUserDetails userDetail) {

        Member member = userDetail.getMember();
        ReportResponseDTO response = reportService.getPatientReport(reportId, member);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "문진 조회 (의사용)", description = "특정 문진을 의사용으로 조회합니다.")
    @GetMapping("/doctor/{reportId}")
    public ResponseEntity<ReportResponseDTO> getDoctorReport(
            @PathVariable("reportId") Long reportId,
            @AuthenticationPrincipal CustomUserDetails userDetail) {

        Member member = userDetail.getMember();
        ReportResponseDTO response = reportService.getDoctorReport(reportId, member);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "문진 조회", description = "특정 문진을 조회합니다.")
    @GetMapping("/{reportId}")
    public ResponseEntity<Map<String, Object>> getReport(
            @PathVariable("reportId") Long reportId,
            @AuthenticationPrincipal CustomUserDetails userDetail) {

        Member member = userDetail.getMember();
        Map<String, Object> response = reportService.getReportForPatientAndDoctor(reportId, member);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "문진 리스트 조회", description = "회원의 모든 문진들을 조회합니다.")
    @GetMapping("/all")
    public ResponseEntity<List<Map<String, Object>>> getAllReports(
            @AuthenticationPrincipal CustomUserDetails userDetail) {

        Member member = userDetail.getMember();
        List<Map<String, Object>> response = reportService.getAllReportsByMember(member);

        return ResponseEntity.ok(response);
    }
}
