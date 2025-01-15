package com.mediko.mediko_server.domain.openai.dto.response;

import com.mediko.mediko_server.domain.openai.domain.SubBodyPart;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SubBodyPartResponseDTO {

    private String body;

    private String description;

    private Long mainBodyPartId;

    public static SubBodyPartResponseDTO fromEntity(SubBodyPart subBodyPart) {
        return new SubBodyPartResponseDTO(
                subBodyPart.getBody(),
                subBodyPart.getDescription(),
                subBodyPart.getMainBodyPart().getId()
        );
    }
}
