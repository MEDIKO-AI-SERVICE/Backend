package com.mediko.mediko_server.domain.openai.domain;

import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.global.domain.BaseEntity;
import com.mediko.mediko_server.global.s3.UuidFile;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @OneToMany(mappedBy = "symptom", cascade = CascadeType.ALL)
    private List<SelectedSign> selectedSigns = new ArrayList<>();

    @OneToMany(mappedBy = "symptom", fetch = FetchType.LAZY)
    private List<UuidFile> uuidFiles = new ArrayList<>();

    // selectedDetailedSign의 sign을 가져오는 메서드
    public List<String> getSelectedSigns() {
        return selectedSigns.stream()
                .flatMap(selectedSign -> selectedSign.getSign().stream())
                .distinct()
                .collect(Collectors.toList());
    }

    // SelectedSBP의 body 부분을 가져오는 메서드
    public List<String> getSelectedSBPBodyParts() {
        return selectedSigns.stream()
                .map(SelectedSign::getSelectedSBP)
                .filter(selectedSBP -> selectedSBP != null)
                .flatMap(selectedSBP -> selectedSBP.getBody().stream())
                .distinct()
                .collect(Collectors.toList());
    }

    // SelectedMBP의 body 부분을 가져오는 메서드
    public List<String> getSelectedMBPBodyParts() {
        return selectedSigns.stream()
                .map(SelectedSign::getSelectedSBP)
                .filter(selectedSBP -> selectedSBP != null)
                .map(SelectedSBP::getSelectedMBP)
                .filter(selectedMBP -> selectedMBP != null)
                .flatMap(selectedMBP -> selectedMBP.getBody().stream())
                .distinct()
                .collect(Collectors.toList());
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
