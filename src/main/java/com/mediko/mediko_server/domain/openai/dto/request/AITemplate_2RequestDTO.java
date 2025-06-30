package com.mediko.mediko_server.domain.openai.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

@Getter
@NoArgsConstructor
public class AITemplate_2RequestDTO {

    private LocalDate startDate;

    @Min(value = 1)
    @Max(value = 6)
    private Integer intensity;

    private Integer durationValue;

    private TimeUnit durationUnit;

    // 10. 추가 설명
    private String additional;

}
