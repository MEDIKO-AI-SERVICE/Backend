package com.mediko.mediko_server.domain.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignOutRequestDTO {
    @NotBlank
    private String accessToken;

    @NotBlank
    private String refreshToken;
}
