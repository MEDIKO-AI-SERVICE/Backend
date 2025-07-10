package com.mediko.mediko_server.global.flask.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FastApiCommunicationService {
    private final RestTemplate restTemplate;

    @Value("${fastapi.url.ai-template}")
    private String aiTemplateUrl;

    @Value("${fastapi.url.medication-template}")
    private String medicationTemplateUrl;

    @Value("${fastapi.url.department-template}")
    private String departmentTemplateUrl;

    @Value("${fastapi.url.adjective}")
    private String adjectiveUrl;

    // AI 템플릿 요청
    public <T, R> R postToAiTemplate(T requestDTO, Class<R> responseType) {
        return post(aiTemplateUrl, requestDTO, responseType);
    }

    // 약 템플릿 요청
    public <T, R> R postToMedicationTemplate(T requestDTO, Class<R> responseType) {
        return post(medicationTemplateUrl, requestDTO, responseType);
    }

    // 진료과 템플릿 요청
    public <T, R> R postToDepartmentTemplate(T requestDTO, Class<R> responseType) {
        return post(departmentTemplateUrl, requestDTO, responseType);
    }

    // 증상 후보 요청
    public <T, R> R postToAdjective(T requestDTO, Class<R> responseType) {
        return post(adjectiveUrl, requestDTO, responseType);
    }


    // 공통 POST 메서드
    public <T, R> R post(String url, T requestDTO, Class<R> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<T> entity = new HttpEntity<>(requestDTO, headers);

        try {
            log.info("FastAPI 요청 URL: {}", url);
            log.info("요청 데이터: {}", requestDTO);

            ResponseEntity<R> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    responseType
            );

            log.info("FastAPI 응답 상태 코드: {}", response.getStatusCode());
            log.info("FastAPI 응답 바디: {}", response.getBody());

            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                throw new RuntimeException("FastAPI 응답 오류");
            }
            return response.getBody();
        } catch (Exception e) {
            log.error("FastAPI 서버 통신 중 오류 발생", e);
            throw new RuntimeException("FastAPI 서버 통신 중 오류 발생", e);
        }
    }

}
