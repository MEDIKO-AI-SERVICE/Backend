package com.mediko.mediko_server.domain.openai.application;

import com.mediko.mediko_server.domain.openai.dto.request.MedicationTemplateRequestDTO;
import com.mediko.mediko_server.domain.openai.dto.request.PatientInfoRequestDTO;

public class MedicationRequestMapper {

    public static MedicationTemplateRequestDTO toDrugTemplateRequestDTO(MedicationProcessingState state) {
        // PatientInfoRequestDTO를 임시로 생성 (DB 저장 안함)
        PatientInfoRequestDTO patientInfo = PatientInfoRequestDTO.builder()
                .language(state.getLanguage())
                .gender(state.getGender())
                .age(state.getAge())
                .allergy(state.getAllergy())
                .familyHistory(state.getFamilyHistory())
                .nowMedicine(state.getNowMedicine())
                .pastHistory(state.getPastHistory())
                .build();

        return MedicationTemplateRequestDTO.builder()
                .isSelf(state.getIsSelf())
                .patientInfo(patientInfo) // 임시로만 사용
                .sign(state.getSign())
                .build();
    }
}