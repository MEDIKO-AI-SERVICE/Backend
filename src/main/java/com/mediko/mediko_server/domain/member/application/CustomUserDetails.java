package com.mediko.mediko_server.domain.member.application;

import com.mediko.mediko_server.domain.member.domain.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomUserDetails implements UserDetails {

    private final Member member;

    public CustomUserDetails(Member member) {
        this.member = member;
    }

    //사용자 권한 정보 반환
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return member.getAuthorities();
    }

    //사용자 비밀번호 반환
    @Override
    public String getPassword() {
        return member.getPassword();
    }

    //사용자 식별자(loginId) 반환
    @Override
    public String getUsername() {
        return member.getUsername();
    }

    //계정 완료 여부 확인
    @Override
    public boolean isAccountNonExpired() {
        return member.isAccountNonExpired();
    }

    //계정 잠금 여부 확인
    @Override
    public boolean isAccountNonLocked() {
        return member.isAccountNonLocked();
    }

    //인증정보(비밀번호) 만료 여부 확인
    @Override
    public boolean isCredentialsNonExpired() {
        return member.isCredentialsNonExpired();
    }

    //계정 활성화 여부 확인
    @Override
    public boolean isEnabled() {
        return member.isEnabled();
    }

    /**
     * 추가 필요한 사용자 정보 조회 메서드
     * Security 기본 인터페이스 외에 필요한 정보들을 조회할 수 있게 함
     */

    public Member getMember() { return this.member; }

    // 사용자 이메일 반환
    public String getEmail() {
        return member.getEmail();
    }


//    //사용자 이메일 인증 여부 확인
//    public Boolean getIsEmailVerified() {
//        return member.getIsEmailVerified();
//    }
//
//    //사용자 전화번호 인증 여부 확인
//    public Boolean getIsPhoneVerified() {
//        return member.getIsPhoneVerified();
//    }
}
