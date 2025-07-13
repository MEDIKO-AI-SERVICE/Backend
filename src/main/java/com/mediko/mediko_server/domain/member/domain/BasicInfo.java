package com.mediko.mediko_server.domain.member.domain;

import com.mediko.mediko_server.domain.member.domain.infoType.Gender;
import com.mediko.mediko_server.domain.member.domain.infoType.Language;
import com.mediko.mediko_server.domain.member.domain.infoType.WeightUnit;
import com.mediko.mediko_server.domain.member.domain.infoType.HeightUnit;
import com.mediko.mediko_server.domain.member.dto.request.BasicInfoRequestDTO;
import com.mediko.mediko_server.global.converter.LanguageConverter;
import com.mediko.mediko_server.global.converter.StringEncryptConverter;
import com.mediko.mediko_server.global.domain.BaseEntity;
import com.mediko.mediko_server.global.exception.exceptionType.BadRequestException;
import jakarta.persistence.*;
import lombok.*;

import static com.mediko.mediko_server.global.exception.ErrorCode.MISSING_REQUIRED_FIELD;

@Entity
@Table(name= "basicInfo")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BasicInfo extends BaseEntity {

    @Convert(converter = StringEncryptConverter.class)
    @Column(name = "er_password")
    private String erPassword;

    @Column(name = "gender")
    private Gender gender;

    @Column(name = "age")
    private Integer age;

    @Column(name = "height")
    private Integer height;

    @Enumerated(EnumType.STRING)
    @Column(name = "height_unit")
    private HeightUnit heightUnit;

    @Column(name = "weight")
    private Integer weight;

    @Enumerated(EnumType.STRING)
    @Column(name = "weight_unit")
    private WeightUnit weightUnit;


    @OneToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    public static BasicInfo createBasicInfo(Member member, Language language, String erPassword) {
        BasicInfo basicInfo = BasicInfo.builder()
                .member(member)
                .erPassword(erPassword)
                .build();

        member.setBasicInfo(basicInfo);

        return basicInfo;
    }


    public void updateBasicInfo(BasicInfoRequestDTO basicInfoRequestDTO) {
        this.gender = basicInfoRequestDTO.getGender();
        this.age = basicInfoRequestDTO.getAge();
        this.height = basicInfoRequestDTO.getHeight();
        this.weight = basicInfoRequestDTO.getWeight();

        validateBasicInfoFields();
    }

    public void validateBasicInfoFields() {
        if (this.age == null ||
                this.gender == null) {
            throw new BadRequestException(MISSING_REQUIRED_FIELD, "필수 입력 항목이 누락되었습니다.");
        }
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }
    public void setAge(Integer age) {
        this.age = age;
    }
    public void setHeight(Integer height) {
        this.height = height;
    }
    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public void setWeightUnit(WeightUnit weightUnit) {
        this.weightUnit = weightUnit;
    }
    public void setHeightUnit(HeightUnit heightUnit) {
        this.heightUnit = heightUnit;
    }
    public WeightUnit getWeightUnit() {
        return weightUnit;
    }
    public HeightUnit getHeightUnit() {
        return heightUnit;
    }
    
    public void setErPassword(String erPassword) {
        this.erPassword = erPassword;
    }

    protected void setMember(Member member) {
        this.member = member;
        if (member != null && member.getBasicInfo() != this) {
            member.setBasicInfo(this);
        }
    }
}