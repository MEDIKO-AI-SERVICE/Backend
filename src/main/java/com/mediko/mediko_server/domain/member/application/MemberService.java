package com.mediko.mediko_server.domain.member.application;

import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.member.domain.BasicInfo;
import com.mediko.mediko_server.domain.member.domain.TempMember;
import com.mediko.mediko_server.domain.member.domain.infoType.Language;
import com.mediko.mediko_server.domain.member.domain.repository.MemberRepository;
import com.mediko.mediko_server.domain.member.domain.repository.BasicInfoRepository;
import com.mediko.mediko_server.domain.member.domain.repository.TempMemberRepository;
import com.mediko.mediko_server.domain.member.dto.request.SignUpRequestDTO;
import com.mediko.mediko_server.domain.member.dto.request.TokenRequestDTO;
import com.mediko.mediko_server.domain.member.dto.request.LanguageRequestDTO;
import com.mediko.mediko_server.domain.member.dto.response.TokenResponseDTO;
import com.mediko.mediko_server.domain.member.dto.response.FormInputResponseDTO;
import com.mediko.mediko_server.domain.member.dto.response.UserInfoResponseDTO;
import com.mediko.mediko_server.global.exception.exceptionType.BadRequestException;
import com.mediko.mediko_server.global.exception.exceptionType.UnauthorizedException;
import com.mediko.mediko_server.global.redis.RedisUtil;
import com.mediko.mediko_server.global.security.JwtTokenProvider;
import com.mediko.mediko_server.global.flask.application.FlaskCommunicationService;
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
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.mediko.mediko_server.global.exception.ErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;
    private final BasicInfoRepository basicInfoRepository;
    private final TempMemberRepository tempMemberRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisUtil redisUtil;
    private final FlaskCommunicationService flaskCommunicationService;

    // 언어 설정 시 임시 멤버 ID 발급
    @Transactional
    public Long setLanguageBeforeSignUp(LanguageRequestDTO languageRequestDTO) {
        TempMember tempMember = TempMember.builder()
                .language(languageRequestDTO.getLanguage())
                .expiredAt(LocalDateTime.now().plusHours(24)) // 24시간 유효
                .isUsed(false)
                .build();
        
        TempMember savedTempMember = tempMemberRepository.save(tempMember);
        return savedTempMember.getId();
    }

    // 119 비밀번호 생성
    private String generate119Password() {
        return flaskCommunicationService.generate119Password();
    }

    //회원 가입
    @Transactional
    public UserInfoResponseDTO signUp(SignUpRequestDTO signUpRequestDTO, Long tempMemberId) {
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

        TempMember tempMember = tempMemberRepository.findByIdAndIsUsedFalse(tempMemberId)
                .orElseThrow(() -> new BadRequestException(INVALID_PARAMETER, "유효하지 않은 임시 멤버 ID입니다."));
        
        if (tempMember.isExpired()) {
            throw new BadRequestException(INVALID_PARAMETER, "만료된 임시 멤버 ID입니다.");
        }

        String encodedPassword = passwordEncoder.encode(signUpRequestDTO.getPassword());

        Member member = signUpRequestDTO.toEntity(encodedPassword);
        member.changeLanguage(tempMember.getLanguage());
        Member savedMember = memberRepository.save(member);

        tempMember.markAsUsed();
        tempMemberRepository.save(tempMember);

        return UserInfoResponseDTO.fromEntity(savedMember);
    }


    //로그인
    @Transactional
    public TokenResponseDTO signIn(String loginId, String password) {
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new BadRequestException(DATA_NOT_EXIST, "등록되지 않은 아이디입니다."));

        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new BadRequestException(INVALID_CREDENTIALS, "비밀번호가 올바르지 않습니다.");
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginId, password);
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        TokenResponseDTO tokenResponseDTO = jwtTokenProvider.generateToken(authentication);

        return tokenResponseDTO;
    }


    //로그아웃
    @Transactional
    public void signOut(HttpServletRequest request) {
        String token = resolveToken(request);
        if (token != null && jwtTokenProvider.validateToken(token)) {
            String username = jwtTokenProvider.getUserNameFromToken(token);

            redisUtil.deleteValues(username);

            Long expiration = jwtTokenProvider.getExpirationTime(token);
            redisUtil.setValues("BLACKLIST:" + token, "logout", Duration.ofMillis(expiration));

            SecurityContextHolder.clearContext();
        }
    }


    // 요청 헤더에서 JWT Token 추출
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }


    // 토큰 재발급
    @Transactional
    public TokenResponseDTO reissueToken(TokenRequestDTO tokenRequestDTO) {
        if (tokenRequestDTO.getRefreshToken() == null) {
            throw new UnauthorizedException(INVALID_TOKEN, "refresh token이 없습니다.");
        }

        return jwtTokenProvider.reissueToken(tokenRequestDTO);
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