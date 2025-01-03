package com.mediko.mediko_server.domain.openai.domain;

import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.openai.dto.request.SelectedMBPRequestDTO;
import com.mediko.mediko_server.global.converter.StringListConvert;
import com.mediko.mediko_server.global.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name= "selectedMbp")
public class SelectedMBP extends BaseEntity {
    @Column(nullable = false)
    @Convert(converter = StringListConvert.class)
    private List<String> body;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    public void updateSelectedMBP(SelectedMBPRequestDTO requestDTO) {
        if (requestDTO.getBody() == null) {
            this.body = new ArrayList<>();
            return;
        }
        this.body = requestDTO.getBody();
    }
}
