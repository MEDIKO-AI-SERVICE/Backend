package com.mediko.mediko_server.domain.recommend.domain.filter;

public enum DepartmentTitle {
    FAMILY_MEDICINE("가정의학과"),
    INTERNAL_MEDICINE("내과"),
    ANESTHESIOLOGY("마취통증의학과"),
    UROLOGY("비뇨의학과"),
    OBSTETRICS_GYNECOLOGY("산부인과"),
    PLASTIC_SURGERY("성형외과"),
    PEDIATRICS("소아청소년과"),
    NEUROLOGY("신경과"),
    NEUROSURGERY("신경외과"),
    OPHTHALMOLOGY("안과"),
    RADIOLOGY("영상의학과"),
    SURGERY("외과"),
    EMERGENCY_MEDICINE("응급의학과"),
    OTORHINOLARYNGOLOGY("이비인후과"),
    REHABILITATION_MEDICINE("재활의학과"),
    PSYCHIATRY("정신건강의학과"),
    ORTHOPEDICS("정형외과"),
    DENTISTRY("치의과"),
    DERMATOLOGY("피부과"),
    ORIENTAL_MEDICINE("한방과"),
    CARDIOTHORACIC_VASCULAR_SURGERY("심장혈관흉부외과");

    private final String value;

    DepartmentTitle(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static DepartmentTitle from(String koreanName) {
        for (DepartmentTitle title : values()) {
            if (title.getValue().equals(koreanName)) {
                return title;
            }
        }
        throw new IllegalArgumentException("일치하는 진료과가 없습니다: " + koreanName);
    }
}
