package com.mediko.mediko_server.global.s3;

import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.openai.domain.AITemplate;
import com.mediko.mediko_server.global.converter.StringEncryptConverter;
import com.mediko.mediko_server.global.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "uuid_image")
public class UuidFile extends BaseEntity {
    @Column(name = "uuid", unique = true)
    private String uuid;

    @Enumerated(EnumType.STRING)
    @Column(name = "file_path")
    private FilePath filePath;

    @Convert(converter = StringEncryptConverter.class)
    @Column(name = "file_url")
    private String fileUrl;

    @Column(name = "session_id")
    private String sessionId;

    @ManyToOne
    @JoinColumn(name = "ai_id")
    private AITemplate aiTemplate;

    @OneToOne
    @JoinColumn(name = "profile_id")
    private Member member;

    // 업데이트 메서드 추가
    public void updateForResult(AITemplate aiTemplate) {
        this.aiTemplate = aiTemplate;
        this.sessionId = null;
    }

    // 필요하다면 파일 URL 등 다른 필드도 추가적으로 업데이트 가능
    public void updateFileInfo(String fileUrl, FilePath filePath) {
        this.fileUrl = fileUrl;
        this.filePath = filePath;
    }


}
