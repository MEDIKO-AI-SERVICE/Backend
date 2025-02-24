package com.mediko.mediko_server.domain.member.dto.request;

import com.mediko.mediko_server.domain.member.domain.BasicInfo;
import com.mediko.mediko_server.domain.member.domain.infoType.Gender;
import com.mediko.mediko_server.domain.member.domain.infoType.Language;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BasicInfoRequestDTO {

    //@NotBlank
    @NotNull(message = "사용언어는 필수 입력 값입니다.")
    private Language language;

    @NotBlank(message = "전화번호는 필수 입력 값입니다.")
    @Pattern(regexp = "^\\d{10,11}$", message = "전화번호 형식이 올바르지 않습니다.")
    private String number;

    @NotBlank(message = "주소는 필수 입력 값입니다.")
    private String address;

    //@NotBlank
    @NotNull(message = "성별은 필수 입력 값입니다.")
    private Gender gender;

    @Positive(message = "나이는 양수로 입력해주세요")
    private Integer age;

    @Positive(message = "키는 양수로 입력해주세요")
    private Integer height;

    @Positive(message = "몸무게는 양수로 입력해주세요")
    private Integer weight;

    public BasicInfo toEntity() {
        return BasicInfo.builder()
                .language(this.language)
                .number(this.number)
                .address(this.address)
                .gender(this.gender)
                .age(this.age)
                .height(this.height)
                .weight(this.weight)
                .build();
    }
}
