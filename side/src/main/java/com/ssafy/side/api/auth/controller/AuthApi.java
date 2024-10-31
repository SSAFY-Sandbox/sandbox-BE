package com.ssafy.side.api.auth.controller;

import com.ssafy.side.api.auth.dto.LoginAccessTokenDto;
import com.ssafy.side.api.auth.dto.SocialLoginRequestDto;
import com.ssafy.side.api.member.dto.GetMemberInfoResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.security.Principal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Auth - 인증/인가 관련 API", description = "Auth API Document")
public interface AuthApi {
    /**
     * [AT - Header, RT - Cookie] 방식
     */
    @Operation(summary = "[AT - Header, RT - Cookie] 로그인", description = "카카오 소셜 로그인 API 입니다.")
    ResponseEntity<LoginAccessTokenDto> socialLoginWithHeaderAndCookie(@RequestBody SocialLoginRequestDto requestDto);

    @Operation(summary = "[AT - Header, RT - Cookie] / [AT - Cookie, RT - Cookie] 토큰 재발급", description = "토큰을 재발급 하는 API 입니다.")
    ResponseEntity<LoginAccessTokenDto> reissueTokenWithCookie(HttpServletRequest request);

    @Operation(summary = "[AT - Header, RT - Cookie] 로그아웃", description = "로그아웃 API 입니다.")
    ResponseEntity<Void> logoutWithHeaderAndCookie(HttpServletRequest request, HttpServletResponse response);

    /**
     * [AT - Header, RT - Header] 방식
     */
    @Operation(summary = "[AT - Header, RT - Header] 로그인", description = "카카오 소셜 로그인 API 입니다.")
    ResponseEntity<LoginAccessTokenDto> socialLoginWithHeaderAndHeader(@RequestBody SocialLoginRequestDto requestDto);

    @Operation(summary = "[AT - Header, RT - Header] 토큰 재발급", description = "토큰을 재발급 하는 API 입니다.")
    ResponseEntity<LoginAccessTokenDto> reissueTokenWithHeader(HttpServletRequest request);

    @Operation(summary = "[AT - Header, RT - Header] 로그아웃", description = "로그아웃 API 입니다.")
    ResponseEntity<Void> logoutWithHeaderAndHeader(HttpServletRequest request);

    /**
     * [AT - Cookie, RT - Cookie] 방식
     */
    @Operation(summary = "[AT - Cookie, RT - Cookie] 로그인", description = "카카오 소셜 로그인 API 입니다.")
    ResponseEntity<LoginAccessTokenDto> socialLoginWithCookieAndCookie(@RequestBody SocialLoginRequestDto requestDto);

    @Operation(summary = "[AT - Cookie, RT - Cookie] 로그아웃", description = "로그아웃 API 입니다.")
    ResponseEntity<Void> logoutWithCookieAndCookie(HttpServletRequest request, HttpServletResponse response);

    @Operation(description = "유저 닉네임을 조회하는 API 입니다.")
    ResponseEntity<GetMemberInfoResponseDto> getMemberInfo(Principal principal);
}
