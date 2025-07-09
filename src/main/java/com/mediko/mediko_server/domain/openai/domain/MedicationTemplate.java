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

    @Column(name = "sign", nullable = false)
    private String sign;

    @Column(name = "drug_name", nullable = false)
    private String drugName;

    @Column(name = "drug_purpose", nullable = false)
    private String drugPurpose;

    @Column(name = "drug_image_url")
    private String drugImageUrl;

    @Column(name = "wrap_image_url")
    private String wrapImageUrl;

    @Column(name = "pharmacist_question1")
    private String pharmacistQuestion1;

    @Column(name = "pharmacist_question2")
    private String pharmacistQuestion2;

    @Column(name = "pharmacist_question3")
    private String pharmacistQuestion3;

    @Column(name = "session_id")
    private String sessionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
}