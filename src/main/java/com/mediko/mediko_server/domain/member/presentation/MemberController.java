package com.mediko.mediko_server.domain.member.presentation;

import com.mediko.mediko_server.domain.member.application.CustomUserDetails;
import com.mediko.mediko_server.domain.member.application.MemberService;
import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.member.dto.request.*;
import com.mediko.mediko_server.domain.member.dto.response.FormInputResponseDTO;
import com.mediko.mediko_server.domain.member.dto.response.TokenResponseDTO;
import com.mediko.mediko_server.domain.member.dto.response.UserInfoResponseDTO;
import com.mediko.mediko_server.domain.member.dto.response.LanguageResponseDTO;
import com.mediko.mediko_server.domain.member.dto.response.UserProfileResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Tag(name = "member", description = "인증/인가 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
public class MemberController {
    private final MemberService memberService;

    @Operation(summary = "회원2. 회원 가입", description = "신규 회원을 등록합니다.")
    @PostMapping("/sign-up")
    public ResponseEntity<UserInfoResponseDTO> signUp(
            @RequestBody SignUpRequestDTO signUpRequestDTO,
            @RequestParam(value = "tempMemberId") Long tempMemberId) {
        UserInfoResponseDTO responseDTO = memberService.signUp(signUpRequestDTO, tempMemberId);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @Operation(summary = "회원1. 언어 설정", description = "회원가입 전 언어를 설정합니다.")
    @PostMapping("/set-language")
    public ResponseEntity<Map<String, Long>> setLanguage(@RequestBody LanguageRequestDTO languageRequestDTO) {
        Long tempMemberId = memberService.setLanguageBeforeSignUp(languageRequestDTO);
        return ResponseEntity.ok(Map.of("tempMemberId", tempMemberId));
    }


    @Operation(summary = "사용자 언어 변경", description = "사용자의 언어를 변경합니다.")
    @PatchMapping("/language")
    public ResponseEntity<LanguageResponseDTO> updateLanguage(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody LanguageRequestDTO languageRequestDTO) {
        Member member = customUserDetails.getMember();
        LanguageResponseDTO responseDTO = memberService.updateLanguage(member, languageRequestDTO);
        return ResponseEntity.ok(responseDTO);
    }


    @Operation(summary = "회원5. 로그인", description = "등록된 회원을 로그인시킵니다.")
    @PostMapping("/sign-in")
    public ResponseEntity<TokenResponseDTO> signIn(@RequestBody SignInRequestDTO signInRequestDTO) {
        TokenResponseDTO tokenResponseDTO = memberService.signIn(signInRequestDTO.getLoginId(), signInRequestDTO.getPassword());
        return ResponseEntity.ok(tokenResponseDTO);
    }


    @Operation(summary = "로그아웃", description = "현재 로그인 된 회원을 로그아웃 시킵니다.")
    @PostMapping("/sign-out")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        memberService.signOut(request);
        return ResponseEntity.ok().build();
    }


    @Operation(summary = "토큰 재발급", description = "RefreshToken으로 AccessToken을 재발급합니다.")
    @PostMapping("/reissue")
    public ResponseEntity<TokenResponseDTO> reissueToken(
            @RequestBody TokenRequestDTO tokenRequestDTO) {
        TokenResponseDTO newToken = memberService.reissueToken(tokenRequestDTO);
        return ResponseEntity.ok(newToken);
    }

    @Operation(summary = "회원 탈퇴", description = "현재 로그인 된 회원을 탈퇴시킵니다.")
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteAccount(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            HttpServletRequest request, HttpServletResponse response) {
        String loginId = customUserDetails.getUsername();
        memberService.deleteAccount(loginId, request, response);
        return ResponseEntity.ok().build();

    }


    @Operation(summary = "폼 입력정보 조회", description = "회원의 119폼 입력정보를 조회합니다.")
    @GetMapping("/form")
    public ResponseEntity<FormInputResponseDTO> getFormInputResponse(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Member member = userDetails.getMember();
        FormInputResponseDTO responseDTO = memberService.getFormInputResponse(member);
        return ResponseEntity.ok(responseDTO);
    }

    @Operation(summary = "사용자 프로필 통합 조회", description = "회원정보, 기본정보, 건강정보, 언어정보를 통합적으로 조회합니다.")
    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponseDTO> getUserProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

    Member member = userDetails.getMember();
    UserProfileResponseDTO responseDTO = memberService.getUserProfile(member);
        return ResponseEntity.ok(responseDTO);
}

    @Operation(summary = "사용자 member_id 조회", description = "로그인한 사용자의 member_id를 조회합니다.")
    @GetMapping("/my-id")
    public ResponseEntity<Map<String, Long>> getMyMemberId(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Member member = customUserDetails.getMember();
        Long memberId = member.getId();
        return ResponseEntity.ok(Map.of("memberId", memberId));
    }

}
