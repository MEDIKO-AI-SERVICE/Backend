package com.mediko.mediko_server.domain.openai.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mediko.mediko_server.domain.openai.domain.MedicationTemplate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MedicationTemplateResponseDTO {

    @JsonProperty("drug_name")
    private String drugName;

    @JsonProperty("drug_purpose")
    private String drugPurpose;

    @JsonProperty("drug_image_url")
    private String drugImageUrl;

    @JsonProperty("wrap_image_url")
    private String wrapImageUrl;;

    @JsonProperty("pharmacist_question1")
    private String pharmacistQuestion1;;

    @JsonProperty("pharmacist_question2")
    private String pharmacistQuestion2;;

    @JsonProperty("pharmacist_question3")
    private String pharmacistQuestion3;

    public static MedicationTemplateResponseDTO fromEntity(MedicationTemplate medication) {
        return new MedicationTemplateResponseDTO(
                medication.getDrugName(),
                medication.getDrugPurpose(),
                medication.getDrugImageUrl(),
                medication.getWrapImageUrl(),
                medication.getPharmacistQuestion1(),
                medication.getPharmacistQuestion2(),
                medication.getPharmacistQuestion3()
        );
    }
}
