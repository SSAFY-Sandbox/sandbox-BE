package com.ssafy.side.api.auth.controller;

import com.ssafy.side.api.auth.dto.LoginAccessTokenDto;
import com.ssafy.side.api.auth.dto.SocialLoginRequestDto;
import com.ssafy.side.api.auth.dto.SocialLoginResponseDto;
import com.ssafy.side.api.auth.service.AuthService;
import com.ssafy.side.api.member.dto.GetMemberInfoResponseDto;
import com.ssafy.side.api.member.service.MemberService;
import com.ssafy.side.common.util.MemberUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/oauth")
@RequiredArgsConstructor
public class AuthController implements AuthApi {

    private final AuthService authService;
    private final MemberService memberService;

    /**
     * [AT - Header, RT - Cookie] 방식
     */

    @PostMapping("/auth")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<LoginAccessTokenDto> socialLoginWithHeaderAndCookie(@RequestBody SocialLoginRequestDto requestDto) {
        SocialLoginResponseDto responseDto = authService.socialLogin(requestDto);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, createCookie("refreshToken", responseDto.refreshToken()).toString());

        return ResponseEntity.status(HttpStatus.CREATED)
                .headers(headers)
                .body(new LoginAccessTokenDto(responseDto.accessToken(), null));
    }

    @GetMapping({"/reissue", "/cookie/reissue"})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<LoginAccessTokenDto> reissueTokenWithCookie(HttpServletRequest request) {
        String accessToken = (String) request.getAttribute("newAccessToken");

        return ResponseEntity.status(HttpStatus.CREATED).body(new LoginAccessTokenDto(accessToken , null));
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> logoutWithHeaderAndCookie(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = (String) request.getAttribute("refreshToken");
        authService.logout(refreshToken);

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) { // refreshToken 쿠키만 삭제
                    cookie.setMaxAge(0);
                    cookie.setValue(null);
                    cookie.setPath("/");
                    response.addCookie(cookie);
                }
            }
        }
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * [AT - Header, RT - Header] 방식
     */

    @PostMapping("/authorization/auth")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<LoginAccessTokenDto> socialLoginWithHeaderAndHeader(@RequestBody SocialLoginRequestDto requestDto) {
        SocialLoginResponseDto responseDto = authService.socialLogin(requestDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new LoginAccessTokenDto(responseDto.accessToken(), responseDto.refreshToken()));
    }

    @GetMapping("/authorization/reissue")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<LoginAccessTokenDto> reissueTokenWithHeader(HttpServletRequest request) {
        String accessToken = (String) request.getAttribute("newAccessToken");

        return ResponseEntity.status(HttpStatus.CREATED).body(new LoginAccessTokenDto(accessToken , null));
    }

    @PostMapping( "/authorization/logout")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> logoutWithHeaderAndHeader(HttpServletRequest request) {
        String refreshToken = (String) request.getAttribute("refreshToken");
        authService.logout(refreshToken);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * [AT - Cookie, RT - Cookie] 방식
     */

    @PostMapping("/cookie/auth")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<LoginAccessTokenDto> socialLoginWithCookieAndCookie(@RequestBody SocialLoginRequestDto requestDto) {
        SocialLoginResponseDto responseDto = authService.socialLogin(requestDto);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, createCookie("accessToken", responseDto.accessToken()).toString());
        headers.add(HttpHeaders.SET_COOKIE, createCookie("refreshToken", responseDto.refreshToken()).toString());

        return ResponseEntity.status(HttpStatus.CREATED)
                .headers(headers)
                .build();
    }

    @PostMapping({ "/cookie/logout"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> logoutWithCookieAndCookie(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = (String) request.getAttribute("refreshToken");
        authService.logout(refreshToken);

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName()) || "accessToken".equals(cookie.getName())) {
                    cookie.setMaxAge(0);
                    cookie.setValue(null);
                    cookie.setPath("/");
                    response.addCookie(cookie);
                }
            }
        }
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping({"/authorization/member", "/cookie/member", "/member"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<GetMemberInfoResponseDto> getMemberInfo(Principal principal) {
        Long memberId = MemberUtil.getUserId(principal);
        return ResponseEntity.ok(memberService.getMemberInfo(memberId));
    }

    private ResponseCookie createCookie(String name, String value) {
        return ResponseCookie.from(name, value)
                .maxAge(7 * 24 * 60 * 60)
                .path("/")
                .sameSite("None")
                .httpOnly(true)
                .secure(true)
                .build();
    }
}
