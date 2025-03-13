package com.mediko.mediko_server.domain.recommend.application;

import com.mediko.mediko_server.domain.recommend.domain.Condition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConditionService {

    // 모든 특수 조건 조회
    public List<String> getAllConditionDescriptions() {
        return Arrays.stream(Condition.values())
                .map(Condition::getDescription)
                .toList();
    }
}
