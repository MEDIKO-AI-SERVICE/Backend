package com.mediko.mediko_server.domain.map.application;

import com.mediko.mediko_server.domain.map.domain.SelectedEr;
import com.mediko.mediko_server.domain.map.domain.repository.SelectedErRepository;
import com.mediko.mediko_server.domain.map.dto.response.ErWithMapUrlDTO;
import com.mediko.mediko_server.domain.map.dto.response.MapUrlResponseDTO;
import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.recommend.domain.Er;
import com.mediko.mediko_server.domain.recommend.domain.repository.ErRepository;
import com.mediko.mediko_server.domain.recommend.dto.response.ErResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SelectedErService {

    private final SelectedErRepository selectedErRepository;
    private final ErRepository erRepository;

    @Value("${app.name}")
    private String appName;

    public ErWithMapUrlDTO getErWithMapUrls(Long erId, Member member) {
        Er er = erRepository.findById(erId)
                .orElseThrow(() -> new IllegalArgumentException("해당 응급실을 찾을 수 없습니다."));

        // 지도 URL 생성
        MapUrlResponseDTO mapUrls = generateMapUrls(er);

        // 응급실 정보 생성
        ErResponseDTO erInfo = ErResponseDTO.fromEntity(er);

        // 사용자가 인증된 경우 응급실 선택 정보 저장
        if (member != null) { saveSelectedEr(er, member, mapUrls); }

        // 응급실 정보와 지도 URL 반환
        return new ErWithMapUrlDTO(erInfo, mapUrls);
    }

    // 응급실 선택 정보 저장
    private void saveSelectedEr(Er er, Member member, MapUrlResponseDTO mapUrls) {
        selectedErRepository.save(
                SelectedEr.builder()
                        .er(er)
                        .member(member)
                        .naverMap(mapUrls.getNaverMap())
                        .kakaoMap(mapUrls.getKakaoMap())
                        .googleMap(mapUrls.getGoogleMap())
                        .build()
        );
    }

    //지도 URL 생성
    private MapUrlResponseDTO generateMapUrls(Er er) {
        double userLatitude = er.getUserLatitude();
        double userLongitude = er.getUserLongitude();
        double erLatitude = er.getErLatitude();
        double erLongitude = er.getErLongitude();
        String erName = er.getName();

        String naverMapUrl = MapUrlGenerator.generateNaverMapUrl(
                userLatitude, userLongitude, erLatitude, erLongitude, erName, appName
        );
        String kakaoMapUrl = MapUrlGenerator.generateKakaoMapUrl(
                userLatitude, userLongitude, erLatitude, erLongitude
        );
        String googleMapUrl = MapUrlGenerator.generateGoogleMapUrl(
                userLatitude, userLongitude, erLatitude, erLongitude
        );

        return new MapUrlResponseDTO(naverMapUrl, kakaoMapUrl, googleMapUrl);
    }
}
