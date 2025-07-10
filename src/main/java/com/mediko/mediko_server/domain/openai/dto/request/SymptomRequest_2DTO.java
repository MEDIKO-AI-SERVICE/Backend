package com.mediko.mediko_server.domain.openai.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mediko.mediko_server.domain.openai.domain.unit.Intensity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class SymptomRequest_2DTO {

    private Intensity intensity;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    private String additional;
}
