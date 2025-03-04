package com.mediko.mediko_server.domain.openai.dto.response;

import com.mediko.mediko_server.domain.openai.domain.SelectedSBP;
import com.mediko.mediko_server.domain.openai.domain.SubBodyPart;
import com.mediko.mediko_server.domain.openai.domain.repository.SubBodyPartRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class SelectedSBPResponseDTO {

    private List<String> description;

    private Long selectedSBPId;

    private Long selectedMBPId;

    public static SelectedSBPResponseDTO fromEntity(
            SelectedSBP selectedSBP, SubBodyPartRepository subBodyPartRepository) {
        List<SubBodyPart> subBodyParts = subBodyPartRepository.findAllById(selectedSBP.getSbpIds());

        List<String> description = subBodyParts.stream()
                .map(SubBodyPart::getDescription)
                .collect(Collectors.toList());

        return new SelectedSBPResponseDTO(
                description,
                selectedSBP.getId(),
                selectedSBP.getSelectedMBP().getId()
        );
    }
}
