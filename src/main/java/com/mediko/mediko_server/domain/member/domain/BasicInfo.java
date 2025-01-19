package com.mediko.mediko_server.domain.member.domain;

import com.mediko.mediko_server.domain.member.domain.infoType.Gender;
import com.mediko.mediko_server.domain.member.domain.infoType.Language;
import com.mediko.mediko_server.domain.member.dto.request.BasicInfoRequestDTO;
import com.mediko.mediko_server.global.domain.BaseEntity;
import com.mediko.mediko_server.global.exception.exceptionType.BadRequestException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.mediko.mediko_server.global.exception.ErrorCode.MISSING_REQUIRED_FIELD;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name= "basicInfo")
public class BasicInfo extends BaseEntity {
    @Column(name = "language", nullable = false)
    private Language language;

    @Column(name = "number", nullable = false)
    private String number;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "gender", nullable = false)
    private Gender gender;

    @Column(name = "age", nullable = false)
    private Integer age;

    @Column(name = "height", nullable = false)
    private Integer height;

    @Column(name = "weight", nullable = false)
    private Integer weight;

    @OneToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    public void validateBasicInfoFields() {
        if (this.language == null ||
                this.number == null || this.number.isBlank() ||
                this.address == null || this.address.isBlank() ||
                this.gender == null) {
            throw new BadRequestException(MISSING_REQUIRED_FIELD, "필수 입력 항목이 누락되었습니다.");
        }
    }

    public void updateBasicInfo(BasicInfoRequestDTO basicInfoRequestDTO) {
        this.language = basicInfoRequestDTO.getLanguage();
        this.number = basicInfoRequestDTO.getNumber();
        this.address = basicInfoRequestDTO.getAddress();
        this.gender = basicInfoRequestDTO.getGender();
        this.age = basicInfoRequestDTO.getAge();
        this.height = basicInfoRequestDTO.getHeight();
        this.weight = basicInfoRequestDTO.getWeight();

        validateBasicInfoFields();
    }
}
