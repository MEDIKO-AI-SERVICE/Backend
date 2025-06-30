package com.mediko.mediko_server.domain.openai.domain;

import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.global.converter.StringEncryptConverter;
import com.mediko.mediko_server.global.domain.BaseEntity;
import com.mediko.mediko_server.global.s3.UuidFile;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "symptom")
public class Symptom extends BaseEntity {

    @Column(name = "startDate", nullable = false)
    private LocalDate startDate;

    @Column(name = "intensity", nullable = false)
    private Integer intensity;

    @Column(name = "duration_v", nullable = false)
    private Integer durationValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "duration_u", nullable = false)
    private TimeUnit durationUnit;

    @Convert(converter = StringEncryptConverter.class)
    @Column(name = "additional")
    private String additional;

    @OneToMany(mappedBy = "symptom", fetch = FetchType.LAZY)
    private List<UuidFile> uuidFiles = new ArrayList<>();

}
