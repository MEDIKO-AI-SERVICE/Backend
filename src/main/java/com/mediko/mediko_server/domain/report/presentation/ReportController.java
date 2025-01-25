package com.mediko.mediko_server.domain.report.presentation;

import com.mediko.mediko_server.domain.report.application.ReportService;
import com.mediko.mediko_server.domain.report.dto.request.ReportRequestDTO;
import com.mediko.mediko_server.domain.report.dto.response.ReportResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/report")
public class ReportController {
    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<ReportResponseDTO> generateReport(
            @RequestBody ReportRequestDTO reportRequestDTO) {
        ReportResponseDTO response = reportService.generateReport(reportRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
