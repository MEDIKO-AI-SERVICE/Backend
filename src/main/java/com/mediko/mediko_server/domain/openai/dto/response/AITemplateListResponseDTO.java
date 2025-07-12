package com.mediko.mediko_server.domain.openai.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mediko.mediko_server.domain.openai.domain.AITemplate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AITemplateListResponseDTO {

    @JsonProperty("ai_id")
    private Long aiTemplateId;

    @JsonProperty("created_at_kst")
    private String createdAtKst;

    @JsonProperty("summary")
    private String summary;

    public static AITemplateListResponseDTO fromEntity(AITemplate aiTemplate) {
        return AITemplateListResponseDTO.builder()
                .aiTemplateId(aiTemplate.getId())
                .createdAtKst(aiTemplate.getCreatedAtKst())
                .summary(aiTemplate.getSummary())
                .build();
    }

    public static List<AITemplateListResponseDTO> fromEntityList(List<AITemplate> aiTemplates) {
        return aiTemplates.stream()
                .map(AITemplateListResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
} 