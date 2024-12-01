package com.mediko.mediko_server.domain.member.dto.response;

import com.mediko.mediko_server.domain.member.domain.Member;
import lombok.Getter;

@Getter
public class UserInfoResponseDTO {
    private String loginId;
    private String email;
    private String name;
    private String nickname;

    // Entity -> DTO 변환
    public static UserInfoResponseDTO fromEntity(Member member) {
        UserInfoResponseDTO response = new UserInfoResponseDTO();
        response.loginId = member.getLoginId();
        response.email = member.getEmail();
        response.name = member.getName();
        response.nickname = member.getNickname();
        return response;
    }
}
