package com.mediko.mediko_server.domain.openai.domain;

import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.global.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "department_template")
public class DepartmentTemplate extends BaseEntity {

    @Column(name = "sign", columnDefinition = "TEXT")
    private String sign;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Enumerated(EnumType.STRING)
    private Intensity intensity;

    @Column(name = "department_re", columnDefinition = "TEXT")
    private String departmentRecommendation;

    @Column(name = "department_de", columnDefinition = "TEXT")
    private String departmentDescription;

    @Column(name = "questions_hp", columnDefinition = "TEXT")
    private String questionsForDoctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
}
