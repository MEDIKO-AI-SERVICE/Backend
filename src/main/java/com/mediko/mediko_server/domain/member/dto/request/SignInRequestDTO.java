package com.mediko.mediko_server.domain.member.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignInRequestDTO {
    private String loginId;
    private String password;
}
