package com.mediko.mediko_server.global.s3.application;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.mediko.mediko_server.global.exception.exceptionType.BadRequestException;
import com.mediko.mediko_server.global.exception.exceptionType.InternalServerErrorException;
import com.mediko.mediko_server.global.exception.exceptionType.ServiceUnavailableException;
import com.mediko.mediko_server.global.s3.FilePath;
import com.mediko.mediko_server.global.s3.UuidFile;
import com.mediko.mediko_server.global.s3.config.S3Config;
import com.mediko.mediko_server.global.s3.repository.UuidFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

import static com.mediko.mediko_server.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UuidFileService {

    private final UuidFileRepository uuidFileRepository;
    private final S3Config s3Config;
    private final AmazonS3 amazonS3;

    public UuidFile findUuidFileById(Long id) {
        return uuidFileRepository.findById(id)
                .orElseThrow(() -> new ServiceUnavailableException(DATA_UNAVAILABLE, "해당 파일을 찾을 수 없습니다."));
    }

    private String generateUniqueUuid() {
        String uuid;
        do {
            uuid = UUID.randomUUID().toString();
        } while (uuidFileRepository.findByUuid(uuid).isPresent());
        return uuid;
    }

    @Transactional
    public UuidFile saveFile(MultipartFile file, FilePath filePath) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException(DATA_NOT_EXIST, "파일이 비어있습니다.");
        }

        String uuid = generateUniqueUuid();
        String keyName = generatePathKey(filePath, uuid);

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());

        try {
            amazonS3.putObject(new PutObjectRequest(
                    s3Config.getBucket(),
                    keyName,
                    file.getInputStream(),
                    objectMetadata
            ));
        } catch (IOException e) {
            throw new InternalServerErrorException(FILE_UPLOAD_FAIL, "파일 변환 중 오류가 발생했습니다: " + e.getMessage());
        } catch (Exception e) {
            throw new InternalServerErrorException(FILE_UPLOAD_FAIL, "S3에 파일 업로드를 실패했습니다: " + e.getMessage());
        }

        String fileS3Url = amazonS3.getUrl(s3Config.getBucket(), keyName).toString();

        return UuidFile.builder()
                .uuid(uuid)
                .filePath(filePath)
                .fileUrl(fileS3Url)
                .build();
    }

    @Transactional
    public void deleteFile(UuidFile uuidFile) {
        if (uuidFile == null) {
            throw new BadRequestException(DATA_NOT_EXIST, "삭제할 파일 정보가 없습니다.");
        }

        String keyName = generatePathKey(uuidFile.getFilePath(), uuidFile.getUuid());

        try {
            if (amazonS3.doesObjectExist(s3Config.getBucket(), keyName)) {
                amazonS3.deleteObject(s3Config.getBucket(), keyName);
            }
        } catch (Exception e) {
            throw new InternalServerErrorException(FILE_DELETE_FAIL, "S3에서 파일 삭제를 실패했습니다: " + e.getMessage());
        }

        uuidFileRepository.delete(uuidFile);
    }

    private String generatePathKey(FilePath filePath, String uuid) {
        return filePath.getPath() + '/' + uuid;
    }
}