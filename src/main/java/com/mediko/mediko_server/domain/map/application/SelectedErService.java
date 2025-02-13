package com.mediko.mediko_server.domain.map.application;

import com.mediko.mediko_server.domain.map.domain.SelectedEr;
import com.mediko.mediko_server.domain.map.domain.repository.SelectedErRepository;
import com.mediko.mediko_server.domain.map.dto.response.MapUrlResponseDTO;
import com.mediko.mediko_server.domain.map.dto.response.SelectedErResponseDTO;
import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.recommend.domain.Er;
import com.mediko.mediko_server.domain.recommend.domain.repository.ErRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SelectedErService {
    /*

    private final SelectedErRepository selectedErRepository;
    private final ErRepository erRepository;

    @Value("${app.name}")
    private String appName;

    // 응급실 선택 및 저장
    public SelectedErResponseDTO saveSelectedEr(Long erId, Member member, String appName) {
        Er er = erRepository.findById(erId)
                .orElseThrow(() -> new IllegalArgumentException("해당 응급실을 찾을 수 없습니다."));

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

        SelectedEr selectedEr = selectedErRepository.save(
                SelectedEr.builder()
                        .er(er)
                        .member(member)
                        .naverMap(naverMapUrl)
                        .kakaoMap(kakaoMapUrl)
                        .googleMap(googleMapUrl)
                        .build()
        );

        return SelectedErResponseDTO.fromEntity(er, selectedEr);
    }

    // 지도 URL 조회
    public MapUrlResponseDTO getMapUrlsForEr(Long erId) {
        Er er = erRepository.findById(erId)
                .orElseThrow(() -> new IllegalArgumentException("해당 응급실을 찾을 수 없습니다."));

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

    */
}
