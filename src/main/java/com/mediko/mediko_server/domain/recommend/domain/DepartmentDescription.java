package com.mediko.mediko_server.domain.recommend.domain;

public enum DepartmentDescription {
    FAMILY_MEDICINE("건강증진, 예방, 만성질환 등"),
    INTERNAL_MEDICINE("감기, 소화기, 호흡기 등"),
    ANESTHESIOLOGY("근육통증, 만성통증 등"),
    UROLOGY("소변시 통증, 남성 질환 등"),
    OBSTETRICS_GYNECOLOGY("피임상담, 여성질환 등"),
    PLASTIC_SURGERY("피부관리, 화상, 상처 등"),
    PEDIATRICS("소아호흡기, 소아소화기, 알레르기 등"),
    NEUROLOGY("두통, 어지럼증, 뇌졸중 등"),
    NEUROSURGERY("요통, 디스크, 신경질환 등"),
    OPHTHALMOLOGY("눈 피로, 결막염, 다래끼 등"),
    RADIOLOGY("방사선 촬영, MRI, CT 등"),
    SURGERY("갑상선, 유방, 하지정맥류 등"),
    EMERGENCY_MEDICINE("심한 탈수, 골절 처치 등"),
    OTORHINOLARYNGOLOGY("비염, 이염, 편도염 등"),
    REHABILITATION_MEDICINE("신체 회복, 물리 치료, 만성통증 등"),
    PSYCHIATRY("수면장애, 스트레스, 중독 등"),
    ORTHOPEDICS("관절염, 골반, 척추 통증 등"),
    DENTISTRY("치아치료, 외과질환, 턱관절 등"),
    DERMATOLOGY("두드러기, 가려움증, 여드름 등"),
    ORIENTAL_MEDICINE("한의원 진료, 다이어트, 침술 등"),
    CARDIOTHORACIC_VASCULAR_SURGERY("심장질환, 혈관질환, 폐질환 등");

    private final String value;

    DepartmentDescription(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
