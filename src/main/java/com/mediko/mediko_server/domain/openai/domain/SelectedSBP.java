package com.mediko.mediko_server.domain.openai.domain;

import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.openai.dto.request.SelectedSBPRequestDTO;
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
@Table(name= "selectedSbp")
public class SelectedSBP extends BaseEntity {
    @Convert(converter = StringListConvert.class)
    @Column(nullable = false)
    private List<String> body;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Convert(converter = LongListConverter.class)
    @Column(name = "sbp_id", nullable = false)
    private List<Long> sbpIds;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selected_mbp_id")
    private SelectedMBP selectedMBP;

    public void updateSelectedSBP(SelectedSBPRequestDTO requestDTO, List<Long> sbpIds, SelectedMBP selectedMBP) {
        if (requestDTO.getBody() == null) {
            this.body = new ArrayList<>();
            this.sbpIds = new ArrayList<>();
        } else {
            this.body = new ArrayList<>(requestDTO.getBody());
            this.sbpIds = new ArrayList<>(sbpIds);
        }
        this.selectedMBP = selectedMBP;
    }
}
