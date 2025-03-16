package com.mediko.mediko_server.domain.map.application;

import com.mediko.mediko_server.domain.map.domain.SelectedEr;
import com.mediko.mediko_server.domain.map.domain.repository.SelectedErRepository;
import com.mediko.mediko_server.domain.map.dto.response.ErWithMapUrlDTO;
import com.mediko.mediko_server.domain.map.dto.response.MapUrlResponseDTO;
import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.recommend.domain.Er;
import com.mediko.mediko_server.domain.recommend.domain.repository.ErRepository;
import com.mediko.mediko_server.domain.recommend.dto.response.GeocodeResponseDTO;
import com.mediko.mediko_server.domain.recommend.dto.response.ErResponseDTO;
import com.mediko.mediko_server.global.flask.application.FlaskCommunicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SelectedErService {

    private final SelectedErRepository selectedErRepository;
    private final ErRepository erRepository;
    private final FlaskCommunicationService flaskCommunicationService;

    @Value("${app.name}")
    private String appName;

    @Transactional
    public ErWithMapUrlDTO getErWithMapUrls(Long erId, Member member) {
        Er er = findErById(erId);
        double userLat;
        double userLon;

        Double userLatitude = er.getUserLatitude();
        Double userLongitude = er.getUserLongitude();

        if (userLatitude != null && userLatitude != 0.0 &&
                userLongitude != null && userLongitude != 0.0) {
            userLat = userLatitude;
            userLon = userLongitude;
        }
        else {
            if (member != null && member.getBasicInfo() != null &&
                    member.getBasicInfo().getAddress() != null) {
                GeocodeResponseDTO coordinates = getCoordinatesFromAddress(
                        member.getBasicInfo().getAddress());
                userLat = coordinates.getLatitude();
                userLon = coordinates.getLongitude();
            } else {
                throw new IllegalArgumentException("사용자의 주소 정보가 없습니다.");
            }
        }

        var result = ErWithMapUrlDTO.fromEntity(
                ErResponseDTO.fromEntity(er),
                er,
                userLat,
                userLon,
                appName
        );

        saveSelectedErIfNeeded(er, member, result.getMapUrls());
        return result;
    }

    private GeocodeResponseDTO getCoordinatesFromAddress(String address) {
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("address", address);
        return flaskCommunicationService.getAddressToCoords(requestData);
    }

    private Er findErById(Long erId) {
        return erRepository.findById(erId)
                .orElseThrow(() -> new IllegalArgumentException("해당 응급실을 찾을 수 없습니다."));
    }

    private void saveSelectedErIfNeeded(Er er, Member member, MapUrlResponseDTO mapUrls) {
        if (member == null) {
            return;
        }

        SelectedEr selectedEr = SelectedEr.builder()
                .er(er)
                .member(member)
                .naverMap(mapUrls.getNaverMap())
                .kakaoMap(mapUrls.getKakaoMap())
                .googleMap(mapUrls.getGoogleMap())
                .build();

        selectedErRepository.save(selectedEr);
    }
}
