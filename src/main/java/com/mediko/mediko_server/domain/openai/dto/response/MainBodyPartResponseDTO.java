package com.mediko.mediko_server.domain.openai.dto.response;

import com.mediko.mediko_server.domain.openai.domain.MainBodyPart;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MainBodyPartResponseDTO {
    private String body;
    private String description;

    public static MainBodyPartResponseDTO fromEntity(MainBodyPart mainBodyPart) {
        return new MainBodyPartResponseDTO(
                mainBodyPart.getBody(),
                mainBodyPart.getDescription()
        );
    }
}
