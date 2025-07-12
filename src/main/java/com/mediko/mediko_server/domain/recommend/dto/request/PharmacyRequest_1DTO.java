package com.mediko.mediko_server.domain.recommend.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.openai.domain.MedicationTemplate;
import com.mediko.mediko_server.domain.recommend.domain.Pharmacy;
import com.mediko.mediko_server.domain.recommend.domain.filter.SortType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PharmacyRequest_1DTO {
    @JsonProperty("lat")
    private Double userLatitude;

    @JsonProperty("lon")
    private Double userLongitude;

    public Pharmacy toEntity(SortType sortType, Member member) {
        return Pharmacy.builder()
                .userLatitude(this.userLatitude)
                .userLongitude(this.userLongitude)
                .sortType(sortType)
                .member(member)
                .build();
    }

}
