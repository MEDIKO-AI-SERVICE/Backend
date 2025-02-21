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
@Table(name= "main_body_part")
public class MainBodyPart extends BaseEntity {

    @Column(nullable = false)
    private String body;

    @Column(nullable = false)
    private String description;

}
