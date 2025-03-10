package com.mediko.mediko_server.domain.member.dto.response;

import com.mediko.mediko_server.domain.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserInfoResponseDTO {
    private Long memberId;

    private String loginId;

    private String email;

    private String name;

    private String nickname;


    public static UserInfoResponseDTO fromEntity(Member member) {
        return new UserInfoResponseDTO(
                member.getId(),
                member.getLoginId(),
                member.getEmail(),
                member.getName(),
                member.getNickname()
        );
    }

}
