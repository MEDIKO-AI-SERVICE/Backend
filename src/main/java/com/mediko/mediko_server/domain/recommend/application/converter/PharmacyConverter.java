package com.mediko.mediko_server.domain.recommend.application.converter;

import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.recommend.domain.Pharmacy;
import com.mediko.mediko_server.domain.recommend.dto.request.PharmacyRequest_2DTO;
import com.mediko.mediko_server.domain.recommend.dto.response.PharmacyResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class PharmacyConverter {

    //request_1 DTO
    public Pharmacy toEntity(
            PharmacyResponseDTO response,
            com.mediko.mediko_server.domain.recommend.dto.request.PharmacyRequest_1DTO requestDTO,
            com.mediko.mediko_server.domain.recommend.domain.filter.SortType sortType,
            Member member) {
        return Pharmacy.builder()
                .userLatitude(requestDTO.getUserLatitude())
                .userLongitude(requestDTO.getUserLongitude())
                .sortType(com.mediko.mediko_server.domain.recommend.domain.filter.SortType.RECOMMEND)
                .name(response.getName())
                .address(response.getAddress())
                .tel(response.getTel())
                .phLatitude(response.getPhLatitude())
                .phLongitude(response.getPhLongitude())
                .start1(response.getStart1())
                .start2(response.getStart2())
                .start3(response.getStart3())
                .start4(response.getStart4())
                .start5(response.getStart5())
                .start6(response.getStart6())
                .start7(response.getStart7())
                .start8(response.getStart8())
                .close1(response.getClose1())
                .close2(response.getClose2())
                .close3(response.getClose3())
                .close4(response.getClose4())
                .close5(response.getClose5())
                .close6(response.getClose6())
                .close7(response.getClose7())
                .close8(response.getClose8())
                .maping(response.getMaping())
                .travelKm(response.getTravelKm())
                .travelH(response.getTravelH())
                .travelM(response.getTravelM())
                .travelS(response.getTravelS())
                .timestamp(response.getTimestamp())
                .version(response.getVersion())
                .latLon(response.getLatLon())
                .postcdn1(response.getPostcdn1())
                .postcdn2(response.getPostcdn2())
                .dutyetc(response.getDutyetc())
                .similarity(response.getSimilarity())
                .member(member)
                .build();
    }

    //request_2 DTO
    public Pharmacy toEntity(PharmacyResponseDTO response, PharmacyRequest_2DTO requestDTO,
                             Member member) {
        return Pharmacy.builder()
                .userLatitude(requestDTO.getUserLatitude())
                .userLongitude(requestDTO.getUserLongitude())
                .sortType(requestDTO.getSortType())
                .name(response.getName())
                .address(response.getAddress())
                .tel(response.getTel())
                .phLatitude(response.getPhLatitude())
                .phLongitude(response.getPhLongitude())
                .start1(response.getStart1())
                .start2(response.getStart2())
                .start3(response.getStart3())
                .start4(response.getStart4())
                .start5(response.getStart5())
                .start6(response.getStart6())
                .start7(response.getStart7())
                .start8(response.getStart8())
                .close1(response.getClose1())
                .close2(response.getClose2())
                .close3(response.getClose3())
                .close4(response.getClose4())
                .close5(response.getClose5())
                .close6(response.getClose6())
                .close7(response.getClose7())
                .close8(response.getClose8())
                .maping(response.getMaping())
                .travelKm(response.getTravelKm())
                .travelH(response.getTravelH())
                .travelM(response.getTravelM())
                .travelS(response.getTravelS())
                .timestamp(response.getTimestamp())
                .version(response.getVersion())
                .latLon(response.getLatLon())
                .postcdn1(response.getPostcdn1())
                .postcdn2(response.getPostcdn2())
                .dutyetc(response.getDutyetc())
                .similarity(response.getSimilarity())
                .member(member)
                .build();
    }
}
