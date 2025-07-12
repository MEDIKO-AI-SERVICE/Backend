package com.mediko.mediko_server.domain.member.dto.response;

import com.mediko.mediko_server.domain.member.domain.BasicInfo;
import com.mediko.mediko_server.domain.member.domain.infoType.Gender;
import com.mediko.mediko_server.domain.member.domain.infoType.HeightUnit;
import com.mediko.mediko_server.domain.member.domain.infoType.Language;
import com.mediko.mediko_server.domain.member.domain.Member;

import com.mediko.mediko_server.domain.member.domain.infoType.WeightUnit;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BasicInfoResponseDTO {

    private Gender gender;

    private Integer age;

    private Integer height;

    private HeightUnit heightUnit;

    private Integer weight;

    private WeightUnit weightUnit;


    public static BasicInfoResponseDTO fromEntity(BasicInfo basicInfo) {
        return new BasicInfoResponseDTO(
                basicInfo.getGender(),
                basicInfo.getAge(),
                basicInfo.getHeight(),
                basicInfo.getHeightUnit(),
                basicInfo.getWeight(),
                basicInfo.getWeightUnit()
        );
    }
}
