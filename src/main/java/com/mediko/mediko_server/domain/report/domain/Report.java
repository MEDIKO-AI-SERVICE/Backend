package com.mediko.mediko_server.domain.report.domain;

import com.mediko.mediko_server.domain.member.domain.BasicInfo;
import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.openai.domain.Symptom;
import com.mediko.mediko_server.global.converter.JsonConverter;
import com.mediko.mediko_server.global.converter.StringListConvert;
import com.mediko.mediko_server.global.converter.StringListMapConverter;
import com.mediko.mediko_server.global.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name= "report")
public class Report extends BaseEntity {
    @Convert(converter = JsonConverter.class)
    @Column(name = "department")
    private Map<String, Object> recommendedDepartment;

    @Convert(converter = JsonConverter.class)
    @Column(name = "conditions")
   private List<Map<String, String>> possibleConditions = new ArrayList<>();

    @Convert(converter = JsonConverter.class)
    @Column(name = "questions")
    private List<Map<String, String>> questionsForDoctor = new ArrayList<>();

    @Convert(converter = JsonConverter.class)
    @Column(name = "checklist")
    private List<Map<String, Object>> symptomChecklist = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "symptom_id", nullable = false)
    private Symptom symptoms;

    @ManyToOne
    @JoinColumn(name = "basic_info_id", nullable = false)
    private BasicInfo basicInfo;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

}
