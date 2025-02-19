package com.mediko.mediko_server.domain.openai.presentation;

import com.mediko.mediko_server.domain.openai.application.DetailedSignService;
import com.mediko.mediko_server.domain.openai.dto.response.DetailedSignResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/detailed-sign")
public class DetailedSignController {
    private final DetailedSignService detailedSignService;

    // 모든 DetailedSign 조회
    @GetMapping("/all")
    public List<DetailedSignResponseDTO> getAllDetailedSigns() {
        return detailedSignService.findAll();
    }

    // 특정 SubBodyPart에 속하는 DetailedSign 조회
    @GetMapping
    public List<DetailedSignResponseDTO> getDetailedSignsBySubBodyPart(
            @RequestParam("body") String body) {
        return detailedSignService.getDetailedSignsByBodyPart(body);
    }
}
