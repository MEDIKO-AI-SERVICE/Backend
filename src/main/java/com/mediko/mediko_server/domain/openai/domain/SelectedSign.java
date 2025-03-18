package com.mediko.mediko_server.domain.openai.domain;

import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.openai.dto.request.SelectedSignRequestDTO;
import com.mediko.mediko_server.global.converter.LongListConverter;
import com.mediko.mediko_server.global.converter.StringListConvert;
import com.mediko.mediko_server.global.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name= "selected_sign")
public class SelectedSign extends BaseEntity {

    @Convert(converter = StringListConvert.class)
    @Column(nullable = false)
    private List<String> sign;

    @Convert(converter = LongListConverter.class)
    @Column(name = "sign_id", nullable = false)
    private List<Long> signIds;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selected_sbp_id", nullable = false)
    private SelectedSBP selectedSBP;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "symptom_id")
    private Symptom symptom;

    public void updateSelectedSign(
            SelectedSignRequestDTO requestDTO, SelectedSBP selectedSBP, List<Long> newSignIds
    ) {
        if (requestDTO.getDescription() == null) {
            this.sign = new ArrayList<>();
            this.signIds = new ArrayList<>();
        } else {
            this.sign = new ArrayList<>(requestDTO.getDescription());
            this.signIds = new ArrayList<>(newSignIds);
        }
        this.selectedSBP = selectedSBP;
    }

    public void updateSymptom(Symptom symptom) {
        this.symptom = symptom;
    }
}
