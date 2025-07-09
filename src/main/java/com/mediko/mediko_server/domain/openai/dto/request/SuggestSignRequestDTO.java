package com.mediko.mediko_server.domain.openai.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mediko.mediko_server.domain.member.domain.infoType.Language;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class SuggestSignRequestDTO {

    private Language language;

    @JsonProperty("body_part")
    private String bodyPart;
}
