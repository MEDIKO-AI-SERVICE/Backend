package com.mediko.mediko_server.domain.member.domain;

import java.util.*;

import com.mediko.mediko_server.global.domain.BaseEntity;
import com.mediko.mediko_server.global.exception.exceptionType.BadRequestException;
import com.mediko.mediko_server.global.s3.UuidFile;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import static com.mediko.mediko_server.global.exception.ErrorCode.*;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name= "member")
public class Member extends BaseEntity implements UserDetails {
    /**
     * 회원가입 정보
     */
    @Column(name = "login_id", nullable = false, unique = true)
    private String loginId;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "nickname", nullable = false, unique = true)
    private String nickname;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL)
    private BasicInfo basicInfo;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL)
    private HealthInfo healthInfo;

    /**
     * UserDetails 인터페이스 메서드
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    //사용자 id 반환
    @Override
    public String getUsername() {
        return loginId;
    }

    //사용자 password 반환
    @Override
    public String getPassword() {
        return password;
    }

    //사용자 계정이 만료되었는지 여부, true: 만료X, false: 만료O
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    //사용자 계정이 잠겨있는지 여부, true: 잠금X, false: 잠금O
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    //패스워드가 만료되었는지 여부, true: 만료X, false: 만료O
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    //사용자의 계정이 활성화되어있는지 여부, true: 활성화O, false: 활성화X
    @Override
    public boolean isEnabled() {
        return true;
    }


    /**
     * 사용자 편의 메서드
     */
    public void changeNickname(String nickname) {
        if (nickname == null || nickname.trim().isEmpty()) {
            throw new BadRequestException(INVALID_PARAMETER, "닉네임은 비어 있을 수 없습니다.");
        }
        this.nickname = nickname;
    }

    /**
     * 연관관계 편의 메서드
     */
    public void setBasicInfo(BasicInfo basicInfo) {
        this.basicInfo = basicInfo;
        if (basicInfo != null && basicInfo.getMember() != this) {
            basicInfo.setMember(this);
        }
    }

    public void setHealthInfo(HealthInfo healthInfo) {
        this.healthInfo = healthInfo;
        if (healthInfo != null && healthInfo.getMember() != this) {
            healthInfo.setMember(this);
        }
    }
}
