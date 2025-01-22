package com.mediko.mediko_server.global.flask;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mediko.mediko_server.domain.report.dto.response.ReportResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
@RequiredArgsConstructor
public class FlaskCommunicationService {
    @Value("${flask.url}")
    private String flaskUrl;

    private final RestTemplate restTemplate;
    private final FlaskUrls flaskUrls;

    public ReportResponseDTO getFlaskResponse(String requestData) {
        String url = UriComponentsBuilder.fromHttpUrl(flaskUrl)
                .path(flaskUrls.getProcessSymptoms())
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> requestEntity = new HttpEntity<>(requestData, headers);

        // 먼저 응답을 String으로 받아서 로그로 확인
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        log.info("Flask Response: {}", responseEntity.getBody());

        // ObjectMapper를 사용해서 수동으로 변환
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(responseEntity.getBody(), ReportResponseDTO.class);
        } catch (JsonProcessingException e) {
            log.error("Error parsing response: ", e);
            throw new RuntimeException("Failed to parse Flask response", e);
        }
    }
}
