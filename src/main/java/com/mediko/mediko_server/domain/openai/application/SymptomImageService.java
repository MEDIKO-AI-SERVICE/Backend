package com.mediko.mediko_server.domain.openai.application;

import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.openai.domain.Symptom;
import com.mediko.mediko_server.domain.openai.domain.repository.SymptomRepository;
import com.mediko.mediko_server.global.exception.exceptionType.BadRequestException;
import com.mediko.mediko_server.global.s3.FilePath;
import com.mediko.mediko_server.global.s3.UuidFile;
import com.mediko.mediko_server.global.s3.UuidFileResponseDTO;
import com.mediko.mediko_server.global.s3.application.UuidFileService;
import com.mediko.mediko_server.global.s3.repository.UuidFileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.mediko.mediko_server.global.exception.ErrorCode.INVALID_PARAMETER;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SymptomImageService {
    private final UuidFileService uuidFileService;
    private final UuidFileRepository uuidFileRepository;
    private final SymptomRepository symptomRepository;

    // 증상 이미지 업로드
    @Transactional
    public List<UuidFileResponseDTO> uploadImages(
            Long symptomId, List<MultipartFile> files, Member member) {
        Symptom symptom = symptomRepository.findByIdAndMemberId(symptomId, member.getId())
                .orElseThrow(() -> new BadRequestException(INVALID_PARAMETER, "증상을 찾을 수 없습니다."));

        if (files == null || files.isEmpty()) {
            throw new BadRequestException(INVALID_PARAMETER, "파일이 비어 있습니다.");
        }

        List<UuidFileResponseDTO> uploadedImages = new ArrayList<>();

        for (MultipartFile file : files) {
            UuidFile savedFile = uuidFileService.saveFile(file, FilePath.SYMPTOM)
                    .toBuilder()
                    .symptom(symptom)
                    .build();

            uploadedImages.add(
                    UuidFileResponseDTO.from(uuidFileRepository.save(savedFile))
            );
        }

        return uploadedImages;
    }

    // 증상 이미지 조회
    public List<UuidFileResponseDTO> getSymptomImages(Long symptomId, Member member) {
        Symptom symptom = symptomRepository.findByIdAndMemberId(symptomId, member.getId())
                .orElseThrow(() -> new BadRequestException(INVALID_PARAMETER, "증상을 찾을 수 없습니다."));

        return uuidFileRepository.findAllBySymptom(symptom)
                .stream()
                .map(UuidFileResponseDTO::from)
                .collect(Collectors.toList());
    }

    // 증상 이미지 개별 삭제
    @Transactional
    public void deleteImage(Long symptomId, Long imageId, Member member) {
        Symptom symptom = symptomRepository.findByIdAndMemberId(symptomId, member.getId())
                .orElseThrow(() -> new BadRequestException(INVALID_PARAMETER, "증상을 찾을 수 없습니다."));

        UuidFile uuidFile = uuidFileService.findUuidFileById(imageId);

        if (!uuidFile.getSymptom().equals(symptom)) {
            throw new BadRequestException(INVALID_PARAMETER, "해당 이미지와 관련된 증상이 일치하지 않습니다.");
        }

        uuidFileService.deleteFile(uuidFile);
    }

    // 증상 이미지 전체 삭제
    @Transactional
    public void deleteAllImages(Long symptomId, Member member) {
        Symptom symptom = symptomRepository.findByIdAndMemberId(symptomId, member.getId())
                .orElseThrow(() -> new BadRequestException(INVALID_PARAMETER, "증상을 찾을 수 없습니다."));

        List<UuidFile> uuidFiles = uuidFileRepository.findAllBySymptom(symptom);

        for (UuidFile file : uuidFiles) {
            uuidFileService.deleteFile(file);
        }
    }
}

