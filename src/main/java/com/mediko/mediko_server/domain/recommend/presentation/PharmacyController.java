package com.mediko.mediko_server.domain.recommend.presentation;

import com.mediko.mediko_server.domain.recommend.application.PharmacyService;
import com.mediko.mediko_server.domain.recommend.dto.request.HospitalRequestDTO;
import com.mediko.mediko_server.domain.recommend.dto.request.PharmacyRequestDTO;
import com.mediko.mediko_server.domain.recommend.dto.response.HospitalResponseDTO;
import com.mediko.mediko_server.domain.recommend.dto.response.PharmacyResponseDTO;
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
@RequestMapping("/api/v1/pharmacy")
public class PharmacyController {
    private final PharmacyService pharmacyService;

    @PostMapping
    public ResponseEntity<List<PharmacyResponseDTO>> recommendPharmacy(
            @RequestBody PharmacyRequestDTO requestDTO
    ) {
        List<PharmacyResponseDTO> responseDTOList = pharmacyService.recommendPharmacy(requestDTO);
        return ResponseEntity.ok(responseDTOList);
    }
}
