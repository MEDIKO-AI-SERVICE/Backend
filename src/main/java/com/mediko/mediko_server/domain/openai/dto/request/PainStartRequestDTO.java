package com.mediko.mediko_server.domain.openai.dto.request;

import com.mediko.mediko_server.domain.openai.domain.TimeUnit;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PainStartRequestDTO {

    private Integer startValue;

    private TimeUnit startUnit;
}
