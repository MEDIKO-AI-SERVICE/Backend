package com.mediko.mediko_server.domain.member.domain;

import com.mediko.mediko_server.domain.member.domain.infoType.Gender;
import com.mediko.mediko_server.domain.member.domain.infoType.Language;
import com.mediko.mediko_server.domain.member.dto.request.BasicInfoRequestDTO;
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
    @Column(name = "language", nullable = false)
    private Language language;

    @Column(name = "number")
    private String number;

    @Column(name = "address")
    private String address;

    @Column(name = "gender")
    private Gender gender;

    @Column(name = "age")
    private Integer age;

    @Column(name = "height")
    private Integer height;

    @Column(name = "weight")
    private Integer weight;

    @Column(name = "er_password", updatable = false)
    private String erPassword;

    @OneToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    public static BasicInfo createBasicInfo(Member member, Language language, String erPassword) {
        BasicInfo basicInfo = BasicInfo.builder()
                .member(member)
                .language(language)
                .erPassword(erPassword)
                .build();

        member.setBasicInfo(basicInfo);

        return basicInfo;
    }


    public void updateBasicInfo(BasicInfoRequestDTO basicInfoRequestDTO) {
        this.number = basicInfoRequestDTO.getNumber();
        this.address = basicInfoRequestDTO.getAddress();
        this.gender = basicInfoRequestDTO.getGender();
        this.age = basicInfoRequestDTO.getAge();
        this.height = basicInfoRequestDTO.getHeight();
        this.weight = basicInfoRequestDTO.getWeight();

        validateBasicInfoFields();
    }

    public void validateBasicInfoFields() {
        if (this.language == null ||
                this.number == null || this.number.isBlank() ||
                this.address == null || this.address.isBlank() ||
                this.gender == null) {
            throw new BadRequestException(MISSING_REQUIRED_FIELD, "필수 입력 항목이 누락되었습니다.");
        }
    }

    public void updateLanguage(Language language) {
        this.language = language;
    }

    protected void setMember(Member member) {
        this.member = member;
        if (member != null && member.getBasicInfo() != this) {
            member.setBasicInfo(this);
        }
    }
}