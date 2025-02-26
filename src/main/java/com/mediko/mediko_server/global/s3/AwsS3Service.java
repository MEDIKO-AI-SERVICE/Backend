package com.mediko.mediko_server.global.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AwsS3Service {
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public AwsS3 upload(MultipartFile multipartFile, String dirName) throws IOException {
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new IllegalArgumentException("업로드할 파일이 없습니다.");
        }

        log.debug("File name: {}", multipartFile.getOriginalFilename());
        log.debug("File size: {}", multipartFile.getSize());
        log.debug("Content type: {}", multipartFile.getContentType());

        File uploadFile = convert(multipartFile)
                .orElseThrow(() -> new IllegalArgumentException("파일 변환에 실패했습니다."));

        try {
            return upload(uploadFile, dirName);
        } catch (Exception e) {
            log.error("S3 업로드 중 에러 발생: ", e);
            throw new IOException("파일 업로드에 실패했습니다.", e);
        }
    }

    private AwsS3 upload(File uploadFile, String dirName) {
        String fileName = dirName + "/" + UUID.randomUUID() + "-" + uploadFile.getName();
        String uploadImageUrl = putS3(uploadFile, fileName);
        removeNewFile(uploadFile);

        return AwsS3.builder()
                .key(fileName)
                .path(uploadImageUrl)
                .build();
    }

    private String putS3(File uploadFile, String fileName) {
        PutObjectRequest request = new PutObjectRequest(bucket, fileName, uploadFile);
        amazonS3.putObject(request);
        return amazonS3.getUrl(bucket, fileName).toString();
    }

    public void deleteFile(String fileName) {
        amazonS3.deleteObject(new DeleteObjectRequest(bucket, fileName));
    }

    private void removeNewFile(File targetFile) {
        if(!targetFile.delete()) {
            log.warn("파일이 삭제되지 못했습니다.");
        }
    }

    private Optional<File> convert(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return Optional.empty();
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            originalFilename = "temp-" + UUID.randomUUID().toString();
        }

        File convertFile = new File(System.getProperty("java.io.tmpdir") + "/" + originalFilename);

        // 동일한 파일명이 있을 경우를 대비해 unique한 파일명 생성
        if (convertFile.exists()) {
            String uniqueFileName = UUID.randomUUID().toString() + "-" + originalFilename;
            convertFile = new File(System.getProperty("java.io.tmpdir") + "/" + uniqueFileName);
        }

        try {
            if (convertFile.createNewFile()) {
                try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                    fos.write(file.getBytes());
                }
                return Optional.of(convertFile);
            }
        } catch (IOException e) {
            log.error("파일 변환 중 에러 발생: ", e);
            throw new IOException("파일 변환에 실패했습니다.", e);
        }

        return Optional.empty();
    }
}