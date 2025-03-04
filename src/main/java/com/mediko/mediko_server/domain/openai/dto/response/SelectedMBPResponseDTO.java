package com.mediko.mediko_server.domain.openai.dto.response;

import com.mediko.mediko_server.domain.openai.domain.MainBodyPart;
import com.mediko.mediko_server.domain.openai.domain.SelectedMBP;
import com.mediko.mediko_server.domain.openai.domain.repository.MainBodyPartRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class SelectedMBPResponseDTO {

    private List<String> description;

    private Long selectedMBPId;

    public static SelectedMBPResponseDTO fromEntity(
            SelectedMBP selectedMBP, MainBodyPartRepository mainBodyPartRepository) {
        List<MainBodyPart> mainBodyParts = mainBodyPartRepository.findAllById(selectedMBP.getMbpIds());

        List<String> description = mainBodyParts.stream()
                .map(MainBodyPart::getDescription)
                .collect(Collectors.toList());

        return new SelectedMBPResponseDTO(
                description,
                selectedMBP.getId()
        );
    }
}
