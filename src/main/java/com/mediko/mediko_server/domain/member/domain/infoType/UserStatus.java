package com.mediko.mediko_server.domain.member.domain.infoType;

public enum UserStatus {
    /**
     * GUEST : 회원가입 이후 상태
     * USER : 회원가입 이후 사용자 기본 정보(BasicInfo) 입력 상태
     * ADMIN : 서비스 관리자
     */

    ROLE_GUEST,     //게스트
    ROLE_USER,      //사용자
    ROLE_ADMIN;     //관리자
}
