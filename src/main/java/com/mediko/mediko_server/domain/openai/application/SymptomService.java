package com.mediko.mediko_server.domain.openai.application;

import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.openai.domain.Symptom;
import com.mediko.mediko_server.domain.openai.domain.repository.SymptomRepository;
import com.mediko.mediko_server.domain.openai.dto.request.AdditionalInfoRequestDTO;
import com.mediko.mediko_server.domain.openai.dto.request.DurationRequestDTO;
import com.mediko.mediko_server.domain.openai.dto.request.IntensityRequestDTO;
import com.mediko.mediko_server.domain.openai.dto.request.PainStartRequestDTO;
import com.mediko.mediko_server.domain.openai.dto.response.AdditionalInfoResponseDTO;
import com.mediko.mediko_server.domain.openai.dto.response.DurationResponseDTO;
import com.mediko.mediko_server.domain.openai.dto.response.IntensityResponseDTO;
import com.mediko.mediko_server.domain.openai.dto.response.PainStartResponseDTO;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SymptomService {
    private final SymptomRepository symptomRepository;

    /**
     * PainStart 관련 메서드
     */
    @Transactional
    public PainStartResponseDTO savePainStart(PainStartRequestDTO requestDTO, Member member) {
        // 새로운 Symptom 엔티티 생성
        Symptom newSymptom = Symptom.builder()
                .startValue(requestDTO.getStartValue())  // PainStartRequestDTO의 startValue 값 사용
                .startUnit(requestDTO.getStartUnit())    // PainStartRequestDTO의 startUnit 값 사용
                .member(member)  // member 설정
                .build();

        // Symptom 저장
        Symptom savedSymptom = symptomRepository.save(newSymptom);

        // 저장된 데이터를 ResponseDTO로 반환
        return PainStartResponseDTO.fromEntity(savedSymptom);
    }



    public PainStartResponseDTO getPainStart(Long symptomId, Member member) {
        // 특정 사용자의 증상 정보를 조회
        Symptom symptom = symptomRepository.findByIdAndMemberId(symptomId, member.getId())
                .orElseThrow(() -> new EntityNotFoundException("Symptom not found for the given ID and member"));
        return PainStartResponseDTO.fromEntity(symptom);
    }

    @Transactional
    public PainStartResponseDTO updatePainStart(Long symptomId, PainStartRequestDTO requestDTO, Member member) {
        // 기존 엔티티 가져오기
        Symptom existingSymptom = symptomRepository.findByIdAndMemberId(symptomId, member.getId())
                .orElseThrow(() -> new EntityNotFoundException("Symptom not found for the given ID and member"));

        // 기존 엔티티 업데이트
        existingSymptom.updatePainStart(requestDTO.getStartValue(), requestDTO.getStartUnit()); // PainStartRequestDTO 값으로 업데이트

        // 저장
        Symptom savedSymptom = symptomRepository.save(existingSymptom);

        // 저장된 데이터를 ResponseDTO로 반환
        return PainStartResponseDTO.fromEntity(savedSymptom);
    }



    /**
     * Duration 관련 메서드
     */
    @Transactional
    public DurationResponseDTO saveDuration(Long symptomId, DurationRequestDTO requestDTO, Member member) {
        Symptom existingSymptom = symptomRepository.findByIdAndMemberId(symptomId, member.getId())
                .orElseThrow(() -> new EntityNotFoundException("Symptom not found for the given ID and member"));

        Symptom updatedSymptom = existingSymptom.toBuilder()
                .durationValue(requestDTO.getDurationValue())
                .durationUnit(requestDTO.getDurationUnit()) // DTO 값을 반영
                .build();

        Symptom savedSymptom = symptomRepository.save(updatedSymptom);

        return DurationResponseDTO.fromEntity(savedSymptom);
    }

    public DurationResponseDTO getDuration(Long symptomId, Member member) {
        // 특정 사용자의 증상 정보 조회
        Symptom symptom = symptomRepository.findByIdAndMemberId(symptomId, member.getId())
                .orElseThrow(() -> new EntityNotFoundException("Symptom not found for the given ID and member"));
        return DurationResponseDTO.fromEntity(symptom);
    }

    @Transactional
    public DurationResponseDTO updateDuration(Long symptomId, DurationRequestDTO requestDTO, Member member) {
        // 기존 엔티티 가져오기
        Symptom existingSymptom = symptomRepository.findByIdAndMemberId(symptomId, member.getId())
                .orElseThrow(() -> new EntityNotFoundException("Symptom not found for the given ID and member"));

        // 기존 엔티티 업데이트
        existingSymptom.updateDuration(requestDTO.getDurationValue(), requestDTO.getDurationUnit()); // Duration 값 업데이트

        // 저장
        Symptom savedSymptom = symptomRepository.save(existingSymptom);

        return DurationResponseDTO.fromEntity(savedSymptom);
    }


    /**
     * Intensity 관련 메서드
     */
    @Transactional
    public IntensityResponseDTO saveIntensity(Long symptomId, IntensityRequestDTO requestDTO, Member member) {
        // 기존 엔티티 가져오기
        Symptom existingSymptom = symptomRepository.findByIdAndMemberId(symptomId, member.getId())
                .orElseThrow(() -> new EntityNotFoundException("Symptom not found for the given ID and member"));

        // 기존 엔티티를 복사하고 DTO 값을 업데이트
        Symptom updatedSymptom = existingSymptom.toBuilder()
                .intensity(requestDTO.getIntensity()) // DTO 값으로 업데이트
                .build();

        // 저장
        Symptom savedSymptom = symptomRepository.save(updatedSymptom);

        // 저장된 데이터를 ResponseDTO로 반환
        return IntensityResponseDTO.fromEntity(savedSymptom);
    }

    public IntensityResponseDTO getIntensity(Long symptomId, Member member) {
        // 특정 사용자의 증상 정보 조회
        Symptom symptom = symptomRepository.findByIdAndMemberId(symptomId, member.getId())
                .orElseThrow(() -> new EntityNotFoundException("Symptom not found for the given ID and member"));
        return IntensityResponseDTO.fromEntity(symptom);
    }

    @Transactional
    public IntensityResponseDTO updateIntensity(Long symptomId, IntensityRequestDTO requestDTO, Member member) {
        // 기존 엔티티 가져오기
        Symptom existingSymptom = symptomRepository.findByIdAndMemberId(symptomId, member.getId())
                .orElseThrow(() -> new EntityNotFoundException("Symptom not found for the given ID and member"));

        // 기존 엔티티 업데이트
        existingSymptom.updateIntensity(requestDTO.getIntensity()); // Intensity 값과 Member 값 업데이트

        // 저장
        Symptom savedSymptom = symptomRepository.save(existingSymptom);

        // 저장된 데이터를 ResponseDTO로 반환
        return IntensityResponseDTO.fromEntity(savedSymptom);
    }


    /**
     * AdditionalInfo 관련 메서드
     */
    @Transactional
    public AdditionalInfoResponseDTO saveAdditionalInfo(Long symptomId, AdditionalInfoRequestDTO requestDTO, Member member) {
        // symptomId와 member를 사용하여 해당 증상 조회
        Symptom symptom = symptomRepository.findByIdAndMemberId(symptomId, member.getId())
                .orElseThrow(() -> new EntityNotFoundException("Symptom not found for the given ID and member"));

        // 증상의 additional 정보 업데이트
        symptom.updateAdditionalInfo(requestDTO.getAdditional());

        // 저장
        Symptom savedSymptom = symptomRepository.save(symptom);

        // 저장된 데이터를 ResponseDTO로 반환
        return AdditionalInfoResponseDTO.fromEntity(savedSymptom);
    }


    public AdditionalInfoResponseDTO getAdditionalInfo(Long symptomId, Member member) {
        // 특정 사용자의 증상 정보 조회
        Symptom symptom = symptomRepository.findByIdAndMemberId(symptomId, member.getId())
                .orElseThrow(() -> new EntityNotFoundException("Symptom not found for the given ID and member"));
        return AdditionalInfoResponseDTO.fromEntity(symptom);
    }

    @Transactional
    public AdditionalInfoResponseDTO updateAdditionalInfo(Long symptomId, AdditionalInfoRequestDTO requestDTO, Member member) {
        // 기존 엔티티 가져오기
        Symptom existingSymptom = symptomRepository.findByIdAndMemberId(symptomId, member.getId())
                .orElseThrow(() -> new EntityNotFoundException("Symptom not found for the given ID and member"));

        // 기존 엔티티 업데이트
        existingSymptom.updateAdditionalInfo(requestDTO.getAdditional()); // Additional 정보 업데이트

        // 저장
        Symptom savedSymptom = symptomRepository.save(existingSymptom);

        return AdditionalInfoResponseDTO.fromEntity(savedSymptom);
    }
}
