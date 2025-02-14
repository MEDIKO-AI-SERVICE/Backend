package com.mediko.mediko_server.domain.recommend.presentation;

import com.mediko.mediko_server.domain.recommend.application.GeocodeService;
import com.mediko.mediko_server.domain.recommend.dto.response.GeocodeResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/geocode")
public class GeocodeController {

    private final GeocodeService geocodeService;

    @GetMapping
    public ResponseEntity<GeocodeResponseDTO> getAddressToCoords(
            @RequestParam(value= "address") String address
    ) {
        return ResponseEntity.ok(geocodeService.getAddressToCoords(address));
    }
}
