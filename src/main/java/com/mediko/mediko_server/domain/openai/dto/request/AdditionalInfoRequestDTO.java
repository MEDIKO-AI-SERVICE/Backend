package com.mediko.mediko_server.domain.openai.dto.request;

import com.mediko.mediko_server.domain.openai.domain.Symptom;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AdditionalInfoRequestDTO {

    private String additional;

}
