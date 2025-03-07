package com.mediko.mediko_server.domain.recommend.application.converter;

import com.mediko.mediko_server.domain.member.domain.BasicInfo;
import com.mediko.mediko_server.domain.member.domain.HealthInfo;
import com.mediko.mediko_server.domain.recommend.domain.Hospital;
import com.mediko.mediko_server.domain.recommend.dto.request.HospitalRequestDTO;
import com.mediko.mediko_server.domain.recommend.dto.response.HospitalResponseDTO;
import com.mediko.mediko_server.domain.report.domain.Report;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HospitalConverter {
    public Hospital toEntity(HospitalResponseDTO response, HospitalRequestDTO requestDTO,
                             String department, String suspectedDisease,
                             BasicInfo basicInfo, HealthInfo healthInfo, Report report) {
        return Hospital.builder()
                .isReport(requestDTO.isReport())
                .userDepartment(department)
                .suspectedDisease(requestDTO.isReport() ?
                        List.of(suspectedDisease.split(",")) : requestDTO.getSuspectedDisease())
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
                .departmentMatch(response.getDepartmentMatch())
                .similarity(response.getSimilarity())
                .url(response.getUrl())
                .basicInfo(basicInfo)
                .healthInfo(healthInfo)
                .report(report)
                .member(basicInfo.getMember())
                .build();
    }
}
