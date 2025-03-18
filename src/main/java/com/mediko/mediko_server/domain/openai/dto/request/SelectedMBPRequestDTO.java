package com.mediko.mediko_server.domain.openai.dto.request;

import com.mediko.mediko_server.domain.openai.domain.SelectedMBP;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class SelectedMBPRequestDTO {

    private List<String> description;

    public SelectedMBP toEntity() {
        return SelectedMBP.builder()
                .body(this.description)
                .build();
    }
}
