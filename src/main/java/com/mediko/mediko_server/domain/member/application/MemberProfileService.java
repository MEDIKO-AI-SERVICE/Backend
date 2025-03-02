package com.mediko.mediko_server.domain.member.application;

import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.member.domain.repository.MemberRepository;
import com.mediko.mediko_server.global.exception.exceptionType.BadRequestException;
import com.mediko.mediko_server.global.s3.FilePath;
import com.mediko.mediko_server.global.s3.UuidFile;
import com.mediko.mediko_server.global.s3.application.UuidFileService;
import com.mediko.mediko_server.global.s3.UuidFileResponseDTO;
import com.mediko.mediko_server.global.s3.repository.UuidFileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import static com.mediko.mediko_server.global.exception.ErrorCode.DATA_NOT_EXIST;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberProfileService {
    private final UuidFileService uuidFileService;
    private final UuidFileRepository uuidFileRepository;

    // 프로필 이미지 업로드
    @Transactional
    public UuidFileResponseDTO uploadImage(MultipartFile file, Member member) {
        uuidFileRepository.findByMember(member).ifPresent(existingFile -> {
            uuidFileService.deleteFile(existingFile);
            uuidFileRepository.delete(existingFile);
        });

        UuidFile newFile = uuidFileService.saveFile(file, FilePath.PROFILE)
                .toBuilder()
                .member(member)
                .build();

        UuidFile savedFile = uuidFileRepository.save(newFile);

        return UuidFileResponseDTO.from(savedFile);
    }


    // 프로필 이미지 조회
    public UuidFileResponseDTO getProfileImage(Member member) {
        UuidFile uuidFile = uuidFileRepository.findByMember(member)
                .orElseThrow(() -> new BadRequestException(DATA_NOT_EXIST, "등록된 프로필 이미지가 없습니다."));

        return UuidFileResponseDTO.from(uuidFile);
    }


    // 프로필 이미지 삭제
    @Transactional
    public void deleteImage(Member member) {
        UuidFile currentImage = uuidFileRepository.findByMember(member)
                .orElseThrow(() -> new BadRequestException(DATA_NOT_EXIST, "등록된 프로필 이미지가 없습니다."));

        uuidFileService.deleteFile(currentImage);
        uuidFileRepository.delete(currentImage);
    }
}