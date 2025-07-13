package com.mediko.mediko_server.global.flask.application;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mediko.mediko_server.domain.member.dto.response.BasicInfoResponseDTO;
import com.mediko.mediko_server.domain.member.dto.response.ErPasswordResponseDTO;
import com.mediko.mediko_server.domain.member.dto.response.HealthInfoResponseDTO;
import com.mediko.mediko_server.domain.recommend.dto.response.ErResponseDTO;
import com.mediko.mediko_server.domain.recommend.dto.response.GeocodeResponseDTO;
import com.mediko.mediko_server.domain.recommend.dto.response.HospitalResponseDTO;
import com.mediko.mediko_server.domain.recommend.dto.response.PharmacyResponseDTO;
import com.mediko.mediko_server.global.flask.FlaskUrls;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FlaskCommunicationService {
    private final RestTemplate restTemplate;
    private final FlaskUrls flaskUrls;

    public List<HospitalResponseDTO> getHospitalRecommendation(Map<String, Object> requestData) {
        log.info("Flask 서버 URL: {}", flaskUrls.getRecommendHospital());
        return sendRequestToFlask(requestData, flaskUrls.getRecommendHospital(),
                new ParameterizedTypeReference<List<HospitalResponseDTO>>() {});
    }

    public List<PharmacyResponseDTO> getPharmacyRecommendation(Map<String, Object> requestData) {
        return sendRequestToFlask(requestData, flaskUrls.getRecommendPharmacy(),
                new ParameterizedTypeReference<List<PharmacyResponseDTO>>() {});
    }


    public List<ErResponseDTO> getErRecommendation(Map<String, Object> requestData) {
        log.info("Flask 서버 URL: {}", flaskUrls.getRecommendEr());
        return sendRequestToFlask(requestData, flaskUrls.getRecommendEr(),
                new ParameterizedTypeReference<List<ErResponseDTO>>() {});
    }

    public GeocodeResponseDTO getAddressToCoords(Map<String, Object> requestData) {
        return sendRequestToFlask(requestData, flaskUrls.getGeocode(),
                GeocodeResponseDTO.class);
    }

    public String generate119Password() {
        try {
            log.info("Flask 서버에서 119 비밀번호 생성 요청: {}", flaskUrls.getErPassword());
            
            // AWS Lambda + API Gateway 응답 형식으로 파싱
            ErPasswordResponseDTO response = restTemplate.getForObject(
                    flaskUrls.getErPassword(),
                    ErPasswordResponseDTO.class
            );
            log.info("파싱된 응답: {}", response);
            
            if (response != null && response.getBody() != null) {
                // body 필드에서 password 추출
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode bodyNode = objectMapper.readTree(response.getBody());
                    String password = bodyNode.get("password").asText();
                    
                    log.info("생성된 119 비밀번호: {}", password);
                    return password;
                } catch (Exception e) {
                    log.error("body에서 password 파싱 실패: {}", e.getMessage());
                    throw new RuntimeException("응답에서 password를 추출할 수 없습니다.", e);
                }
            } else {
                log.error("Flask 서버에서 null 응답을 받았습니다.");
                throw new RuntimeException("Flask 서버에서 유효하지 않은 응답을 받았습니다.");
            }
        } catch (Exception e) {
            log.error("Flask 서버 통신 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("Flask 서버 통신 중 오류 발생", e);
        }
    }

    public BasicInfoResponseDTO translateBasicInfo(BasicInfoResponseDTO basicInfo, String language) {
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("language", language);
        requestData.put("basic_info", basicInfo);

        return sendRequestToFlask(requestData, flaskUrls.getTranslateBasicInfo(), BasicInfoResponseDTO.class);
    }

    public HealthInfoResponseDTO translateHealthInfo(HealthInfoResponseDTO healthInfo, String language) {
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("language", language);
        requestData.put("health_info", healthInfo);

        return sendRequestToFlask(requestData, flaskUrls.getTranslateHealthInfo(), HealthInfoResponseDTO.class);
    }

    private <T> T sendRequestToFlask(Object requestData, String endpoint, Class<T> responseType) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Object> entity = new HttpEntity<>(requestData, headers);

            ResponseEntity<T> response = restTemplate.exchange(
                    endpoint,
                    HttpMethod.POST,
                    entity,
                    responseType
            );

            return response.getBody();
        } catch (Exception e) {
            log.error("Flask 서버 통신 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("Flask 서버 통신 중 오류 발생", e);
        }
    }

    private <T> T sendRequestToFlask(Object requestData, String endpoint,
                                     ParameterizedTypeReference<T> responseType) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Object> entity = new HttpEntity<>(requestData, headers);

            log.info("Flask 서버로 보내는 데이터: {}", requestData);
            log.info("Flask 서버 엔드포인트: {}", endpoint);

            ResponseEntity<T> response = restTemplate.exchange(
                    endpoint,
                    HttpMethod.POST,
                    entity,
                    responseType
            );

            log.info("Flask 서버 응답: {}", response.getBody());

            return response.getBody();
        } catch (Exception e) {
            log.error("Flask 서버 통신 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("Flask 서버 통신 중 오류 발생", e);
        }
    }
}