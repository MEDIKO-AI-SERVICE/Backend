package com.mediko.mediko_server.domain.openai.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.time.LocalDate;

public class HospitalTemplateRequestDTO {

    private String sign;

    private LocalDate startDate;

    @Min(value = 1)
    @Max(value = 6)
    private Integer intensity;
}
