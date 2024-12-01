package com.mediko.mediko_server.domain.member.domain.repository;

import com.mediko.mediko_server.domain.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.User;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    //아이디로 회원 조회
    Optional<Member> findByLoginId(String loginId);

    //이메일 중복 여부 확인
    Boolean existsByEmail(String email);

    //닉네임 중복 여부 확인
    Boolean existsByNickname(String nickname);
}
