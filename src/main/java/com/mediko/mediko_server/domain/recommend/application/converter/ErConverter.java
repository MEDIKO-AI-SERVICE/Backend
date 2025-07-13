package com.mediko.mediko_server.domain.recommend.application.converter;

import com.mediko.mediko_server.domain.member.domain.BasicInfo;
import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.recommend.domain.Er;
import com.mediko.mediko_server.domain.recommend.dto.request.ErRequestDTO;
import com.mediko.mediko_server.domain.recommend.dto.response.ErResponseDTO;
import org.springframework.stereotype.Component;


@Component
public class ErConverter {
    public Er toEntity(ErResponseDTO response, ErRequestDTO requestDTO, Member member) {
        return Er.builder()
                .name(response.getName())
                .address(response.getAddress())
                .tel(response.getTel())
                .hvamyn(response.getHvamyn())
                .isTrauma(response.getIsTrauma())
                .userLatitude(requestDTO.getUserLatitude())
                .userLongitude(requestDTO.getUserLongitude())
                .erLatitude(response.getErLatitude())
                .erLongitude(response.getErLongitude())
                .conditions(requestDTO.getConditions())
                .travelKm(response.getTravelKm())
                .travelH(response.getTravelH())
                .travelM(response.getTravelM())
                .travelS(response.getTravelS())
                .member(member)
                .build();
    }
}
