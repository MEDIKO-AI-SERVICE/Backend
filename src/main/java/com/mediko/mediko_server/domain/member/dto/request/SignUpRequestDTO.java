package com.mediko.mediko_server.domain.member.dto.request;

import com.mediko.mediko_server.domain.member.domain.infoType.Gender;
import com.mediko.mediko_server.domain.member.domain.Member;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Getter
@NoArgsConstructor
public class SignUpRequestDTO {
    @Length(min = 4, max = 20)
    private String loginId;

    @Length(min = 8, max = 20)
    private String password;

    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    private String email;

    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private String name;

    @NotBlank(message = "닉네임은 필수 입력 값입니다.")
    private String nickname;

    //DTO -> Entity 변환
    public Member toEntity(String encodedPassword) {
        return Member.builder()
                .loginId(this.loginId)
                .password(encodedPassword)
                .email(this.email)
                .name(this.name)
                .nickname(this.nickname)
                .build();
    }
}
