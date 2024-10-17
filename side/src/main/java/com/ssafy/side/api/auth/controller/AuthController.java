package com.ssafy.side.api.auth.controller;

import com.ssafy.side.api.auth.dto.LoginAccessTokenDto;
import com.ssafy.side.api.auth.dto.SocialLoginRequestDto;
import com.ssafy.side.api.auth.dto.SocialLoginResponseDto;
import com.ssafy.side.api.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth - 인증/인가 관련 API", description = "Auth API Document")
public class AuthController {

    private final AuthService authService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(description = "카카오 소셜 로그인을 하는 API 입니다.")
    public ResponseEntity<LoginAccessTokenDto> socialLogin(@RequestBody SocialLoginRequestDto requestDto) {
        SocialLoginResponseDto responseDto = authService.socialLogin(requestDto);
        ResponseCookie cookie = ResponseCookie.from("refreshToken", responseDto.refreshToken())
                .maxAge(7*24*60*60)
                .path("/")
                .sameSite("None")
                .httpOnly(true)
                .secure(true)
                .build();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.status(HttpStatus.CREATED)
        .headers(headers)
        .body(new LoginAccessTokenDto(responseDto.accessToken()));
    }

    @GetMapping("/reissue")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(description = "토큰을 재발급 하는 API 입니다.")
    public ResponseEntity<LoginAccessTokenDto> reissueToken(HttpServletRequest request) {
        String accessToken = (String) request.getAttribute("newAccessToken");
        return ResponseEntity.status(HttpStatus.CREATED).body(new LoginAccessTokenDto(accessToken));
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "로그아웃 API 입니다.")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = (String) request.getAttribute("refreshToken");
        authService.logout(refreshToken, request, response);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
