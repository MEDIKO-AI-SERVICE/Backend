package com.mediko.mediko_server.domain.openai.dto.request;

import com.mediko.mediko_server.domain.openai.domain.SelectedSign;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class SelectedSignRequestDTO {
    private List<String> sign;

    public SelectedSign toEntity() {
        return SelectedSign.builder()
                .sign(this.sign)
                .build();
    }
}
