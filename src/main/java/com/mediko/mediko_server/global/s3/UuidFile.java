package com.mediko.mediko_server.global.s3;

import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.openai.domain.Symptom;
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

    @OneToOne
    @JoinColumn(name = "profile_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "symptom_id")
    private Symptom symptom;
}
