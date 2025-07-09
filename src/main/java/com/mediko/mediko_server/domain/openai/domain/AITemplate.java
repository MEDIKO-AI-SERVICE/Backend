package com.mediko.mediko_server.domain.openai.domain;

import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.openai.domain.unit.Intensity;
import com.mediko.mediko_server.domain.openai.domain.unit.State;

import com.mediko.mediko_server.domain.openai.domain.unit.TimeUnit;
import com.mediko.mediko_server.global.converter.*;
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
import java.util.Map;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ai_template")
public class AITemplate extends BaseEntity {

    @Column(name = "is_self", nullable = false)
    private Boolean isSelf;

    @Column(name = "body_part", nullable = false)
    private String bodyPart;

    @Column(name = "sign", nullable = false)
    @Convert(converter = StringListConvert.class)
    private List<String> selectedSign;

    @Enumerated(EnumType.STRING)
    @Column(name = "intensity", nullable = false)
    private Intensity intensity;

    @Column(name = "startDate", nullable = false)
    private LocalDate startDate;

    @Column(name = "duration_v", nullable = false)
    private Integer durationValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "duration_u", nullable = false)
    private TimeUnit durationUnit;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private State state;

    @Column(name = "additional")
    private String additional;

    @Column(name = "summary")
    private String summary;

    @Column(name = "created_at_kst")
    private String createdAtKst;

    @Column(name = "department")
    private String department;

    @Column(name = "description")
    private String departmentDescription;

    @Convert(converter = StringMapListConverter.class)
    @Column(name = "questions", length = 1000)
    private Map<String, String> questionsToDoctor;

    @Column(name = "symptom_summary")
    private String symptomSummary;

    @Column(name = "session_id")
    private String sessionId;

    @OneToMany(mappedBy = "aiTemplate", fetch = FetchType.LAZY)
    private List<UuidFile> uuidFiles = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
}
