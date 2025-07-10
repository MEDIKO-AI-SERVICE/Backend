package com.mediko.mediko_server.domain.openai.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mediko.mediko_server.domain.openai.domain.unit.Intensity;
import com.mediko.mediko_server.domain.openai.domain.unit.State;
import com.mediko.mediko_server.domain.openai.domain.unit.TimeUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class SymptomRequest_1DTO {

    private Intensity intensity;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    private Integer durationValue;

    private TimeUnit durationUnit;

    private State state;

    private String additional;
}
