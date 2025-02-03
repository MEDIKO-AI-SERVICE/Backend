package com.mediko.mediko_server.domain.recommend.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class HospitalResponseListDTO {
    private List<HospitalResponseDTO> hospitals;
}
