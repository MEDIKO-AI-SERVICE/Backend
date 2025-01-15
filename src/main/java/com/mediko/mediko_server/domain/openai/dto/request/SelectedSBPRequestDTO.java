package com.mediko.mediko_server.domain.openai.dto.request;

import com.mediko.mediko_server.domain.openai.domain.SelectedSBP;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class SelectedSBPRequestDTO {

    private List<String> body;

    public SelectedSBP toEntity() {
        return SelectedSBP.builder()
                .body(this.body)
                .build();
    }
}
