package com.mediko.mediko_server.domain.openai.domain;

import com.mediko.mediko_server.global.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name= "detailed_sign")
public class DetailedSign extends BaseEntity  {
    @Column(nullable = false)
    private String sign;

    @Column(nullable = false)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sbp_id")
    private SubBodyPart subBodyPart;
}
