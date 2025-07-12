package com.mediko.mediko_server.domain.recommend.dto.response;

import com.mediko.mediko_server.domain.recommend.domain.filter.DepartmentDescription;
import com.mediko.mediko_server.domain.recommend.domain.filter.DepartmentTitle;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentResponseDTO {

    private String title;

    private String description;

    public static DepartmentResponseDTO from(DepartmentTitle title) {
        return new DepartmentResponseDTO(
                title.getValue(),
                DepartmentDescription.valueOf(title.name()).getValue()
        );
    }
}
