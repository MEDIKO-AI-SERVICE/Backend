package com.mediko.mediko_server.domain.openai.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class IntensityRequestDTO {

    @Min(value = 1)
    @Max(value = 10)
    private Integer intensity;

}
