package com.mediko.mediko_server.domain.openai.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.concurrent.TimeUnit;

@Getter
@NoArgsConstructor
public class AITemplate_1RequestDTO {

    private boolean isSelf;

    private String bodyPart;

}
