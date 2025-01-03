package com.mediko.mediko_server.domain.openai.dto.response;

import com.mediko.mediko_server.domain.openai.domain.SelectedMBP;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class SelectedMBPResponseDTO {
    private List<String> body;
    private Long memberId;

    public static SelectedMBPResponseDTO fromEntity(SelectedMBP selectedMBP) {
        return new SelectedMBPResponseDTO(
                selectedMBP.getBody(),
                selectedMBP.getMember().getId()
        );
    }
}
