package com.mediko.mediko_server.domain.recommend.application.converter;

import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.openai.domain.DepartmentTemplate;
import com.mediko.mediko_server.domain.recommend.domain.Hospital;
import com.mediko.mediko_server.domain.recommend.dto.request.HospitalRequest_1DTO;
import com.mediko.mediko_server.domain.recommend.dto.request.HospitalRequest_2DTO;
import com.mediko.mediko_server.domain.recommend.dto.response.HospitalResponseDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HospitalConverter {
    // 1. DepartmentTemplate 기반 (HospitalRequest_1DTO)
    public Hospital toEntity(
            HospitalResponseDTO response,
            HospitalRequest_1DTO requestDTO,
            String department,
            boolean primaryHospital,
            boolean secondaryHospital,
            boolean tertiaryHospital,
            Member member
    ) {
        return Hospital.builder()
                .userDepartment(List.of(department))
                .primaryHospital(primaryHospital)
                .secondaryHospital(secondaryHospital)
                .tertiaryHospital(tertiaryHospital)
                .userLatitude(requestDTO.getUserLatitude())
                .userLongitude(requestDTO.getUserLongitude())
                .name(response.getName())
                .telephone(response.getTelephone())
                .hpDepartment(response.getHpDepartment())
                .hpAddress(response.getHpAddress())
                .hpLatitude(response.getHpLatitude())
                .hpLongitude(response.getHpLongitude())
                .sidocdnm(response.getSidocdnm())
                .sggucdnm(response.getSggucdnm())
                .emdongnm(response.getEmdongnm())
                .clcdnm(response.getClcdnm())
                .esDistanceInKm(response.getEsDistanceInKm())
                .travelKm(response.getTravelKm())
                .travelH(response.getTravelH())
                .travelM(response.getTravelM())
                .travelS(response.getTravelS())
                .sortScore(response.getSortScore())
                .similarity(response.getSimilarity())
                .url(response.getUrl())
                .member(member)
                .build();
    }

    // 2. 직접입력 기반 (HospitalRequest_2DTO)
    public Hospital toEntity(
            HospitalResponseDTO response,
            HospitalRequest_2DTO requestDTO,
            List<String> userDepartment,
            Member member
    ) {
        return Hospital.builder()
                .userDepartment(userDepartment)
                .primaryHospital(requestDTO.isPrimaryHospital())
                .secondaryHospital(requestDTO.isSecondaryHospital())
                .tertiaryHospital(requestDTO.isTertiaryHospital())
                .userLatitude(requestDTO.getUserLatitude())
                .userLongitude(requestDTO.getUserLongitude())
                .name(response.getName())
                .telephone(response.getTelephone())
                .hpDepartment(response.getHpDepartment())
                .hpAddress(response.getHpAddress())
                .hpLatitude(response.getHpLatitude())
                .hpLongitude(response.getHpLongitude())
                .sidocdnm(response.getSidocdnm())
                .sggucdnm(response.getSggucdnm())
                .emdongnm(response.getEmdongnm())
                .clcdnm(response.getClcdnm())
                .esDistanceInKm(response.getEsDistanceInKm())
                .travelKm(response.getTravelKm())
                .travelH(response.getTravelH())
                .travelM(response.getTravelM())
                .travelS(response.getTravelS())
                .sortScore(response.getSortScore())
                .similarity(response.getSimilarity())
                .url(response.getUrl())
                .member(member)
                .build();
    }
}

