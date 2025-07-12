package com.mediko.mediko_server.domain.member.dto.response;

import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.member.domain.infoType.Language;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponseDTO {
    private Long memberId;
    private String loginId;
    private String email;
    private String name;
    private String number;
    private String address;
    private Language language;

    public static UserInfoResponseDTO fromEntity(Member member) {
        return new UserInfoResponseDTO(
            member.getId(),
            member.getLoginId(),
            member.getEmail(),
            member.getName(),
            member.getNumber(),
            member.getAddress(),
            member.getLanguage()
        );
    }
}
