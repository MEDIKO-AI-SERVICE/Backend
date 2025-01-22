package com.mediko.mediko_server.domain.openai.domain;

import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.global.converter.LongListConverter;
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
@Table(name = "symptom")
public class Symptom extends BaseEntity {

    @Column(name = "start_v", nullable = false)
    @Builder.Default
    private Integer startValue = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "start_u",nullable = false)
    @Builder.Default
    private TimeUnit startUnit = TimeUnit.DEFAULT;

    @Column(name = "duration_v", nullable = false)
    @Builder.Default
    private Integer durationValue = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "duration_u", nullable = false)
    @Builder.Default
    private TimeUnit durationUnit = TimeUnit.DEFAULT;

    @Column(name = "intensity", nullable = false)
    @Builder.Default
    private Integer intensity = 0;

    @Column(name = "additional")
    private String additional;

//    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
//    @JoinColumn(name = "symptom_id")
//    private List<UuidFile> images = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @OneToMany(mappedBy = "symptom", cascade = CascadeType.ALL)
    private List<SelectedSBP> selectedSBPs = new ArrayList<>();


    // SelectedSBP의 body 부분을 가져오는 메서드
    public List<String> getSelectedSBPBodyParts() {
        List<String> bodyParts = new ArrayList<>();
        for (SelectedSBP selectedSBP : selectedSBPs) {
            bodyParts.addAll(selectedSBP.getBody());  // SelectedSBP의 body를 가져옴
        }
        return bodyParts;
    }

    // SelectedMBP의 body 부분을 가져오는 메서드
    public List<String> getSelectedMBPBodyParts() {
        List<String> bodyParts = new ArrayList<>();
        for (SelectedSBP selectedSBP : selectedSBPs) {
            SelectedMBP selectedMBP = selectedSBP.getSelectedMBP();
            if (selectedMBP != null) {
                bodyParts.addAll(selectedMBP.getBody());  // SelectedMBP의 body를 가져옴
            }
        }
        return bodyParts;
    }

    public void updatePainStart(Integer startValue, TimeUnit painStartUnit) {
        this.startValue = startValue;
        this.startUnit = painStartUnit;
    }

    public void updateDuration(Integer durationValue, TimeUnit durationUnit) {
        this.durationValue = durationValue;
        this.durationUnit = durationUnit;
    }

    public void updateIntensity(Integer intensity) {
        this.intensity = intensity;
    }

    public void updateAdditionalInfo(String additional) {
        this.additional = additional;
    }
}
