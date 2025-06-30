package com.mediko.mediko_server.domain.openai.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SuggestSignRequestDTO {
    private String bodyPart;
}
