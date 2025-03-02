package com.mediko.mediko_server.global.flask.application;

import com.mediko.mediko_server.domain.member.dto.response.ErPasswordResponseDTO;
import com.mediko.mediko_server.domain.recommend.dto.response.ErResponseDTO;
import com.mediko.mediko_server.domain.recommend.dto.response.GeocodeResponseDTO;
import com.mediko.mediko_server.domain.recommend.dto.response.HospitalResponseDTO;
import com.mediko.mediko_server.domain.recommend.dto.response.PharmacyResponseDTO;
import com.mediko.mediko_server.domain.report.dto.response.ReportResponseDTO;
import com.mediko.mediko_server.global.flask.FlaskUrls;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FlaskCommunicationService {
    private final RestTemplate restTemplate;
    private final FlaskUrls flaskUrls;

    public ReportResponseDTO getReportResponse(Object requestData) {
        return sendRequestToFlask(requestData, flaskUrls.getProcessSymptoms(), ReportResponseDTO.class);
    }

    public List<HospitalResponseDTO> getHospitalRecommendation(Map<String, Object> requestData) {
        return sendRequestToFlask(requestData, flaskUrls.getRecommendHospital(),
                new ParameterizedTypeReference<List<HospitalResponseDTO>>() {});
    }

    public List<PharmacyResponseDTO> getPharmacyRecommendation(Map<String, Object> requestData) {
        return sendRequestToFlask(requestData, flaskUrls.getRecommendPharmacy(),
                new ParameterizedTypeReference<List<PharmacyResponseDTO>>() {});
    }

    public List<ErResponseDTO> getErRecommendation(Map<String, Object> requestData) {
        return sendRequestToFlask(requestData, flaskUrls.getRecommendEr(),
                new ParameterizedTypeReference<List<ErResponseDTO>>() {});
    }

    public GeocodeResponseDTO getAddressToCoords(Map<String, Object> requestData) {
        return sendRequestToFlask(requestData, flaskUrls.getGeocode(),
                GeocodeResponseDTO.class);
    }

    public String generate119Password() {
        try {
            ErPasswordResponseDTO response = restTemplate.getForObject(
                    flaskUrls.getErPassword(),
                    ErPasswordResponseDTO.class
            );
            return response.getPassword();
        } catch (Exception e) {
            log.error("Flask 서버 통신 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("Flask 서버 통신 중 오류 발생", e);
        }
    }

    private <T> T sendRequestToFlask(Object requestData, String endpoint, Class<T> responseType) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Object> entity = new HttpEntity<>(requestData, headers);

            ResponseEntity<T> response = restTemplate.exchange(
                    endpoint,  // 전체 URL이 이미 포함되어 있음
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
                    endpoint,  // 전체 URL이 이미 포함되어 있음
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