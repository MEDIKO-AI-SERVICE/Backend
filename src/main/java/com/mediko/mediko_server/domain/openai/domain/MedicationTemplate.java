package com.mediko.mediko_server.domain.openai.domain;

import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.global.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "medication_template")
public class MedicationTemplate extends BaseEntity {

    @Column(name = "is_self", nullable = false)
    private Boolean isSelf;

    @Column(name = "sign", columnDefinition = "TEXT")
    private String sign;

    @Column(name = "medication_names", columnDefinition = "TEXT")
    private String medicationNames;

    @Column(name = "medication_indications", columnDefinition = "TEXT")
    private String medicationIndications;

    @Column(name = "medication_img_1", columnDefinition = "TEXT")
    private String medicationImageUrls_1;

    @Column(name = "medication_img_2", columnDefinition = "TEXT")
    private String medicationImageUrls_2;

    @Column(name = "questions_ph", columnDefinition = "TEXT")
    private String questionsForPharmacist;

    @Column(name = "warning_message")
    private String warningMessage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
}