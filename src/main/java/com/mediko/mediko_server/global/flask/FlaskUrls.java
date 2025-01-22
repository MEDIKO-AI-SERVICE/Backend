package com.mediko.mediko_server.global.flask;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class FlaskUrls {
    @Value("${flask.urls.process_symptoms}")
    private String processSymptoms;

    @Value("${flask.urls.recommend_er}")
    private String recommendEr;

    @Value("${flask.urls.recommend_pharmacy}")
    private String recommendPharmacy;

    @Value("${flask.urls.recommend_hospital}")
    private String recommendHospital;
}
