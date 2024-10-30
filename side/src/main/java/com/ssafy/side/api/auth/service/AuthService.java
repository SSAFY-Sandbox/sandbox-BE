package com.ssafy.side.api.auth.service;

import com.ssafy.side.api.auth.dto.SocialLoginRequestDto;
import com.ssafy.side.api.auth.dto.SocialLoginResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {

    SocialLoginResponseDto socialLogin(SocialLoginRequestDto requestDto);
    void logout(String refreshToken);

}
