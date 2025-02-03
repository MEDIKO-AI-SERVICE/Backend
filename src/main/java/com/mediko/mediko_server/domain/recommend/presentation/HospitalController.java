package com.mediko.mediko_server.domain.recommend.presentation;

import com.mediko.mediko_server.domain.recommend.application.HospitalService;
import com.mediko.mediko_server.domain.recommend.dto.request.HospitalRequestDTO;
import com.mediko.mediko_server.domain.recommend.dto.response.HospitalResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/hospital")
public class HospitalController {
    private final HospitalService hospitalService;

    @PostMapping
    public ResponseEntity<List<HospitalResponseDTO>> recommendHospital(
            @RequestBody HospitalRequestDTO requestDTO
    ) {
        List<HospitalResponseDTO> responseDTOList = hospitalService.recommendHospital(requestDTO);
        return ResponseEntity.ok(responseDTOList);
    }
}
