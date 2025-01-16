package com.mediko.mediko_server.domain.openai.application;

import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.openai.domain.Symptom;
import com.mediko.mediko_server.domain.openai.domain.TimeUnit;
import com.mediko.mediko_server.domain.openai.domain.repository.SymptomRepository;
import com.mediko.mediko_server.domain.openai.dto.request.AdditionalInfoRequestDTO;
import com.mediko.mediko_server.domain.openai.dto.request.DurationRequestDTO;
import com.mediko.mediko_server.domain.openai.dto.request.IntensityRequestDTO;
import com.mediko.mediko_server.domain.openai.dto.request.PainStartRequestDTO;
import com.mediko.mediko_server.domain.openai.dto.response.AdditionalInfoResponseDTO;
import com.mediko.mediko_server.domain.openai.dto.response.DurationResponseDTO;
import com.mediko.mediko_server.domain.openai.dto.response.IntensityResponseDTO;
import com.mediko.mediko_server.domain.openai.dto.response.PainStartResponseDTO;
import com.mediko.mediko_server.global.exception.exceptionType.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.mediko.mediko_server.global.exception.ErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SymptomService {
    private final SymptomRepository symptomRepository;

    /**
     * PainStart 관련 메서드
     */
    // 증상 시작시간 저장
    @Transactional
    public PainStartResponseDTO savePainStart(PainStartRequestDTO requestDTO, Member member) {
        validateTimeValue(requestDTO.getStartValue(), requestDTO.getStartUnit());

        Symptom newSymptom = Symptom.builder()
                .startValue(requestDTO.getStartValue())
                .startUnit(requestDTO.getStartUnit())
                .member(member)
                .build();

        Symptom savedSymptom = symptomRepository.save(newSymptom);

        return PainStartResponseDTO.fromEntity(savedSymptom);
    }

    // 증상 시작시간 조회
    public PainStartResponseDTO getPainStart(Long symptomId, Member member) {
        Symptom symptom = symptomRepository.findByIdAndMemberId(symptomId, member.getId())
                .orElseThrow(() -> new BadRequestException(DATA_NOT_EXIST, "저장된 증상정보를 찾을 수 없습니다."));
        return PainStartResponseDTO.fromEntity(symptom);
    }

    // 증상 시작시간 수정
    @Transactional
    public PainStartResponseDTO updatePainStart(Long symptomId, PainStartRequestDTO requestDTO, Member member) {

        validateTimeValue(requestDTO.getStartValue(), requestDTO.getStartUnit());

        Symptom existingSymptom = symptomRepository.findByIdAndMemberId(symptomId, member.getId())
                .orElseThrow(() -> new BadRequestException(DATA_NOT_EXIST, "저장된 증상정보를 찾을 수 없습니다."));

        existingSymptom.updatePainStart(requestDTO.getStartValue(), requestDTO.getStartUnit()); // PainStartRequestDTO 값으로 업데이트

        Symptom savedSymptom = symptomRepository.save(existingSymptom);

        return PainStartResponseDTO.fromEntity(savedSymptom);
    }

    /**
     * Intensity 관련 메서드
     */
    //증상 강도 저장
    @Transactional
    public IntensityResponseDTO saveIntensity(Long symptomId, IntensityRequestDTO requestDTO, Member member) {

        Symptom existingSymptom = symptomRepository.findByIdAndMemberId(symptomId, member.getId())
                .orElseThrow(() -> new BadRequestException(DATA_NOT_EXIST, "저장된 증상정보를 찾을 수 없습니다."));

        validatePainStartExists(existingSymptom);

        existingSymptom.updateIntensity(requestDTO.getIntensity());

        Symptom savedSymptom = symptomRepository.save(existingSymptom);

        return IntensityResponseDTO.fromEntity(savedSymptom);
    }

    //증상 강도 조회
    public IntensityResponseDTO getIntensity(Long symptomId, Member member) {
        Symptom symptom = symptomRepository.findByIdAndMemberId(symptomId, member.getId())
                .orElseThrow(() -> new BadRequestException(DATA_NOT_EXIST, "저장된 증상정보를 찾을 수 없습니다."));
        return IntensityResponseDTO.fromEntity(symptom);
    }


    /**
     * Duration 관련 메서드
     */
    //증상 지속기간 저장
    @Transactional
    public DurationResponseDTO saveDuration(Long symptomId, DurationRequestDTO requestDTO, Member member) {

        Symptom existingSymptom = symptomRepository.findByIdAndMemberId(symptomId, member.getId())
                .orElseThrow(() -> new BadRequestException(DATA_NOT_EXIST, "저장된 증상정보를 찾을 수 없습니다."));

        validateIntensityExists(existingSymptom);

        validateTimeValue(requestDTO.getDurationValue(), requestDTO.getDurationUnit());

        existingSymptom.updateDuration(requestDTO.getDurationValue(), requestDTO.getDurationUnit()); // Duration 값 업데이트

        Symptom savedSymptom = symptomRepository.save(existingSymptom);

        return DurationResponseDTO.fromEntity(savedSymptom);
    }

    //증상 지속기간 조회
    public DurationResponseDTO getDuration(Long symptomId, Member member) {

        Symptom symptom = symptomRepository.findByIdAndMemberId(symptomId, member.getId())
                .orElseThrow(() -> new BadRequestException(DATA_NOT_EXIST, "저장된 증상정보를 찾을 수 없습니다."));
        return DurationResponseDTO.fromEntity(symptom);
    }



    /**
     * AdditionalInfo 관련 메서드
     */
    //증상 추가정보 저장
    @Transactional
    public AdditionalInfoResponseDTO saveAdditionalInfo(Long symptomId, AdditionalInfoRequestDTO requestDTO, Member member) {

        Symptom existingSymptom = symptomRepository.findByIdAndMemberId(symptomId, member.getId())
                .orElseThrow(() -> new BadRequestException(DATA_NOT_EXIST, "저장된 증상정보를 찾을 수 없습니다."));

        validateDurationExists(existingSymptom);

        existingSymptom.updateAdditionalInfo(requestDTO.getAdditional());
        Symptom savedSymptom = symptomRepository.save(existingSymptom);

        return AdditionalInfoResponseDTO.fromEntity(savedSymptom);
    }


    //증상 추가 정보 조회
    public AdditionalInfoResponseDTO getAdditionalInfo(Long symptomId, Member member) {

        Symptom symptom = symptomRepository.findByIdAndMemberId(symptomId, member.getId())
                .orElseThrow(() -> new BadRequestException(DATA_NOT_EXIST, "저장된 증상정보를 찾을 수 없습니다."));
        return AdditionalInfoResponseDTO.fromEntity(symptom);
    }


    /**
     * TimeValue + TimeUnit 값 검증
     */

    private void validateTimeValue(int value, TimeUnit timeUnit) {
        if (!timeUnit.isValidValue(value)) {
            throw new BadRequestException(INVALID_FORMAT, value + "값 은 " + timeUnit + " 단위에 유효하지 않습니다.");
        }
    }


    /**
     * 이전 단계 데이터 존재 여부 검증
     */

    private void validatePainStartExists(Symptom symptom) {
        if (symptom.getStartValue() == 0 || symptom.getStartUnit() == TimeUnit.DEFAULT) {
            throw new BadRequestException(MISSING_REQUIRED_FIELD, "통증 시작시간 정보가 없습니다. 먼저 통증 시작시간 정보를 입력해주세요.");
        }
    }

    private void validateIntensityExists(Symptom symptom) {
        validatePainStartExists(symptom);
        if (symptom.getIntensity() == 0) {
            throw new BadRequestException(MISSING_REQUIRED_FIELD, "통증 강도 정보가 없습니다. 먼저 통증 강도 정보를 입력해주세요.");
        }
    }

    private void validateDurationExists(Symptom symptom) {
        validateIntensityExists(symptom);
        if (symptom.getDurationValue() == 0 || symptom.getDurationUnit() == TimeUnit.DEFAULT) {
            throw new BadRequestException(MISSING_REQUIRED_FIELD, "통증 지속기간 정보가 없습니다. 먼저 통증 지속기간 정보를 입력해주세요.");
        }
    }

}
