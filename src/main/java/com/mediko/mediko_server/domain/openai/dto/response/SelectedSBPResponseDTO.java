package com.mediko.mediko_server.domain.openai.dto.response;

import com.mediko.mediko_server.domain.openai.domain.SelectedSBP;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class SelectedSBPResponseDTO {

    private List<String> body;

    private Long selectedSBPId;

    private Long selectedMBPId;

    public static SelectedSBPResponseDTO fromEntity(SelectedSBP selectedSBP) {
        return new SelectedSBPResponseDTO(
                selectedSBP.getBody(),
                selectedSBP.getId(),
                selectedSBP.getSelectedMBP().getId()
        );
    }
}
