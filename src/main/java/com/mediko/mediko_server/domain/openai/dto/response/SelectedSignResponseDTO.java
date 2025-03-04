package com.mediko.mediko_server.domain.openai.dto.response;


import com.mediko.mediko_server.domain.openai.domain.DetailedSign;
import com.mediko.mediko_server.domain.openai.domain.SelectedSign;
import com.mediko.mediko_server.domain.openai.domain.repository.DetailedSignRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class SelectedSignResponseDTO {

    private List<String> description;

    private Long signId;

    private Long selectedSBPId;

    public static SelectedSignResponseDTO fromEntity(
            SelectedSign selectedSign, DetailedSignRepository detailedSignRepository) {
        List<DetailedSign> detailedSign = detailedSignRepository.findAllById(selectedSign.getSignIds());

        List<String> description = detailedSign.stream()
                .map(DetailedSign::getDescription)
                .collect(Collectors.toList());

        return new SelectedSignResponseDTO(
                description,
                selectedSign.getId(),
                selectedSign.getSelectedSBP().getId()
        );
    }
}

