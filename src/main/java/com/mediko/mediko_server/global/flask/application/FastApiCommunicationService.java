package com.mediko.mediko_server.global.flask.application;

import com.mediko.mediko_server.domain.openai.dto.request.MedicationTemplateRequestDTO;
import com.mediko.mediko_server.domain.openai.dto.response.MedicationTemplateResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class FastApiCommunicationService {
    private final RestTemplate restTemplate;

//    @Value("${fastapi.url.drug-template}")
    private String fastApiUrl;

    public MedicationTemplateResponseDTO requestDrugTemplate(MedicationTemplateRequestDTO requestDTO) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MedicationTemplateRequestDTO> entity = new HttpEntity<>(requestDTO, headers);

        try {
            ResponseEntity<MedicationTemplateResponseDTO> response = restTemplate.exchange(
                    fastApiUrl,
                    HttpMethod.POST,
                    entity,
                    MedicationTemplateResponseDTO.class
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("FastAPI 서버 통신 중 오류 발생", e);
        }
    }
}
