package com.mediko.mediko_server.domain.recommend.application;

import com.mediko.mediko_server.domain.recommend.dto.response.GeocodeResponseDTO;
import com.mediko.mediko_server.global.flask.FlaskCommunicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GeocodeService {

    private final FlaskCommunicationService flaskCommunicationService;

    public GeocodeResponseDTO getAddressToCoords(String address) {
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("address", address);
        return flaskCommunicationService.getAddressToCoords(requestData);
    }
}
