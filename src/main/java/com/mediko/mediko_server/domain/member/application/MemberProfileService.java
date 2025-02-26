package com.mediko.mediko_server.domain.member.application;

import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.member.domain.repository.MemberRepository;
import com.mediko.mediko_server.global.s3.AwsS3;
import com.mediko.mediko_server.global.s3.AwsS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberProfileService {
    private final AwsS3Service awsS3Service;
    private final MemberRepository memberRepository;

    private static final String PROFILE_IMAGE_DIRECTORY = "profile-images";

    // 프로필 이미지 업로드
    @Transactional
    public void uploadProfileImage(Member member, MultipartFile imageFile) {
        try {
            String currentProfileImg = member.getProfileImg();
            if (currentProfileImg != null) {
                String oldFileName = PROFILE_IMAGE_DIRECTORY + "/" +
                        currentProfileImg.substring(currentProfileImg.lastIndexOf("/") + 1);
                awsS3Service.deleteFile(oldFileName);
            }

            AwsS3 uploadedImage = awsS3Service.upload(imageFile, PROFILE_IMAGE_DIRECTORY);
            member.updateProfileImage(uploadedImage);
            memberRepository.save(member);
        } catch (IOException e) {
            throw new RuntimeException("프로필 이미지 업로드에 실패했습니다.", e);
        }
    }

    // 프로필 이미지 조회
    public String getProfileImageUrl(Member member) {
        String profileImg = member.getProfileImg();
        if (profileImg == null) {
            return null;
        }
        return profileImg;
    }

    // 프로필 이미지 삭제
    @Transactional
    public void deleteProfileImage(Member member) {
        String currentProfileImg = member.getProfileImg();
        if (currentProfileImg != null) {
            String fileName = PROFILE_IMAGE_DIRECTORY + "/" +
                    currentProfileImg.substring(currentProfileImg.lastIndexOf("/") + 1);
            awsS3Service.deleteFile(fileName);
            member.updateProfileImage(null);
            memberRepository.save(member);
        }
    }
}