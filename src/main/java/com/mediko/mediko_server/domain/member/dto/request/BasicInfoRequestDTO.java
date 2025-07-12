package com.mediko.mediko_server.domain.member.dto.request;

import com.mediko.mediko_server.domain.member.domain.BasicInfo;
import com.mediko.mediko_server.domain.member.domain.infoType.Gender;
import com.mediko.mediko_server.domain.member.domain.infoType.WeightUnit;
import com.mediko.mediko_server.domain.member.domain.infoType.HeightUnit;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BasicInfoRequestDTO {

    //@NotBlank
    @NotNull(message = "성별은 필수 입력 값입니다.")
    private Gender gender;

    @Positive(message = "나이는 양수로 입력해주세요")
    private Integer age;

    @Positive(message = "키는 양수로 입력해주세요")
    private Integer height;

    private HeightUnit heightUnit;

    @Positive(message = "몸무게는 양수로 입력해주세요")
    private Integer weight;

    private WeightUnit weightUnit;


    public BasicInfo toEntity() {
        return BasicInfo.builder()
                .gender(this.gender)
                .age(this.age)
                .height(this.height)
                .heightUnit(this.heightUnit)
                .weight(this.weight)
                .weightUnit(this.weightUnit)
                .build();
    }
}
