package com.mediko.mediko_server.domain.member.dto.response;

import com.mediko.mediko_server.domain.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FormInputResponseDTO {

    private String name;

    private String number;

    private String address;

    private String password;

    public static FormInputResponseDTO from(Member member) {
        return new FormInputResponseDTO(
                member.getName(),
                member.getNumber(),
                member.getAddress(),
                member.getPassword()
        );
    }
}
