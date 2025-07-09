package com.mediko.mediko_server.domain.openai.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SuggestSignResponseDTO {
    private List<String> adjectives;
}
