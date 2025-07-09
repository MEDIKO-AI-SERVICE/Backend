package com.mediko.mediko_server.domain.openai.domain;

import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.openai.domain.unit.Intensity;
import com.mediko.mediko_server.global.converter.StringListConvert;
import com.mediko.mediko_server.global.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "department_template")
public class DepartmentTemplate extends BaseEntity {

    @Column(name = "body_part", nullable = false)
    private String bodyPart;

    @Column(name = "sign", nullable = false)
    @Convert(converter = StringListConvert.class)
    private List<String> selectedSign;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "intensity", nullable = false)
    private Intensity intensity;

    @Column(name = "additional")
    private String additional;

    @Column(name = "department")
    private String department;

    @Column(name = "description", length = 1000)
    private String departmentDescription;

    @Convert(converter = StringListConvert.class)
    @Column(name = "questions",  length = 1000)
    private List<String> questionsToDoctor;

    @Column(name = "session_id")
    private String sessionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
}
