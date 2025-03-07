package com.mediko.mediko_server.domain.recommend.presentation;

import com.mediko.mediko_server.domain.recommend.application.GeocodeService;
import com.mediko.mediko_server.domain.recommend.dto.response.GeocodeResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "geocode", description = "주소 변환 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/geocode")
public class GeocodeController {

    private final GeocodeService geocodeService;

    @Operation(summary = "주소 변환", description = "주소를 입력하면 위경도 값을 반환합니다.")
    @GetMapping
    public ResponseEntity<GeocodeResponseDTO> getAddressToCoords(
            @RequestParam("address") String address) {
        return ResponseEntity.ok(geocodeService.getAddressToCoords(address));
    }
}
