package com.mediko.mediko_server.domain.member.application;

import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.member.domain.infoType.UserStatus;
import com.mediko.mediko_server.domain.member.domain.repository.MemberRepository;
import com.mediko.mediko_server.domain.member.dto.request.SignUpRequestDTO;
import com.mediko.mediko_server.domain.member.dto.request.TokenDTO;
import com.mediko.mediko_server.domain.member.dto.response.FormInputResponseDTO;
import com.mediko.mediko_server.domain.member.dto.response.UserInfoResponseDTO;
import com.mediko.mediko_server.global.exception.exceptionType.BadRequestException;
import com.mediko.mediko_server.global.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.mediko.mediko_server.global.exception.ErrorCode.*;
import static org.springframework.http.HttpStatus.CREATED;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;

    //회원 가입
    @Transactional
    public UserInfoResponseDTO signUp(SignUpRequestDTO signUpRequestDTO) {
        if (memberRepository.existsByEmail(signUpRequestDTO.getEmail())) {
            throw new BadRequestException(DATA_ALREADY_EXIST, "이미 사용 중인 이메일입니다.");
        }

        if (memberRepository.existsByNickname(signUpRequestDTO.getNickname())) {
            throw new BadRequestException(DATA_ALREADY_EXIST, "이미 사용 중인 닉네임입니다.");
        }

        if (signUpRequestDTO.getLoginId() == null ||  signUpRequestDTO.getPassword() == null ||
                signUpRequestDTO.getEmail() == null || signUpRequestDTO.getName() == null || signUpRequestDTO.getNickname() == null) {
            throw new BadRequestException(MISSING_REQUIRED_FIELD, "필수 입력 항목이 누락되었습니다.");
        }

        String encodedPassword = passwordEncoder.encode(signUpRequestDTO.getPassword());

        Member member = signUpRequestDTO.toEntity(encodedPassword);
        member.addRole(UserStatus.ROLE_GUEST);
        Member savedMember = memberRepository.save(member);

        return UserInfoResponseDTO.fromEntity(savedMember);
    }

    //로그인
    @Transactional
    public TokenDTO signIn(String loginId, String password) {
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new BadRequestException(DATA_NOT_EXIST, "등록되지 않은 아이디입니다."));

        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new BadRequestException(INVALID_CREDENTIALS, "비밀번호가 올바르지 않습니다.");
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginId, password);
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        TokenDTO tokenDTO = jwtTokenProvider.generateToken(authentication);

        return tokenDTO;
    }


    //로그아웃
    @Transactional
    public void signOut(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);  // 세션을 로그아웃 처리
        }
    }


    // 회원 탈퇴
    @Transactional
    public void deleteAccount(String loginId, HttpServletRequest request, HttpServletResponse response) {
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new BadRequestException(DATA_NOT_EXIST, "존재하지 않는 사용자입니다."));

        memberRepository.delete(member);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
    }

    // 닉네임 조회
    @Transactional(readOnly = true)
    public String getUserNickname(String loginId) {
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new BadRequestException(DATA_NOT_EXIST, "존재하지 않는 사용자입니다."));
        return member.getNickname();
    }


    // 닉네임 변경
    @Transactional
    public void updateUserNickName(String loginId, String nickname) {
        if (memberRepository.existsByNickname(nickname)) {
            throw new BadRequestException(DATA_ALREADY_EXIST, "이미 사용 중인 닉네임입니다.");
        }

        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new BadRequestException(DATA_NOT_EXIST, "존재하지 않는 사용자입니다."));

        member.changeNickname(nickname);
        memberRepository.save(member);
    }


    // 119 폼 입력정보 조회
    @Transactional(readOnly = true)
    public FormInputResponseDTO getFormInputResponse(Member member) {
        return FormInputResponseDTO.from(member);
    }

}
