package com.mediko.mediko_server.domain.openai.dto.response;

import com.mediko.mediko_server.domain.openai.domain.DetailedSign;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DetailedSignResponseDTO {
    private String sign;

    private String description;

    public static DetailedSignResponseDTO fromEntity(DetailedSign detailedSign) {
        return new DetailedSignResponseDTO(
                detailedSign.getSign(),
                detailedSign.getDescription()
        );
    }
}
