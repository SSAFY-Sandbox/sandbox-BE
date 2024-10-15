package com.ssafy.side.common.Filter;

import static com.ssafy.side.common.exception.ErrorMessage.*;

import com.ssafy.side.common.config.jwt.JwtAuthenticationEntryPoint;
import com.ssafy.side.common.config.jwt.JwtExceptionType;
import com.ssafy.side.common.config.jwt.JwtTokenProvider;
import com.ssafy.side.common.config.jwt.UserAuthentication;
import com.ssafy.side.common.exception.UnAuthorizedException;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    private static final String ISSUE_TOKEN_API_URL = "/auth/reissue";
    private static final String LOGOUT_API_URL = "/auth/logout";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        try {
            String accessToken = jwtTokenProvider.resolveToken(request);

            if (ISSUE_TOKEN_API_URL.equals(request.getRequestURI())) {
                handleTokenReissue(request);
            } else if (LOGOUT_API_URL.equals(request.getRequestURI())) {
                handleLogout(request, response);
            } else {
                validateAccessToken(accessToken);
            }
        } catch (UnAuthorizedException e) {
            jwtAuthenticationEntryPoint.setResponse(response, HttpStatus.UNAUTHORIZED, e.getCode().toString());
            return;
        }

        chain.doFilter(request, response);
    }

    private void handleTokenReissue(HttpServletRequest request) {
        String refreshToken = getRefreshTokenFromCookies(request);
        validateRefreshToken(refreshToken);

        Long memberId = jwtTokenProvider.validateMemberRefreshToken(refreshToken);
        String newAccessToken = jwtTokenProvider.generateAccessToken(memberId);

        setAuthentication(newAccessToken);
        request.setAttribute("newAccessToken", newAccessToken);
    }

    private void handleLogout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = getRefreshTokenFromCookies(request);
        validateRefreshToken(refreshToken);

        request.setAttribute("refreshToken", refreshToken);
    }

    private String getRefreshTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            throw new UnAuthorizedException(ERR_NO_COOKIE);
        }
        return Arrays.stream(cookies)
                .filter(cookie -> "refreshToken".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElseThrow(() -> new UnAuthorizedException(ERR_NO_REFRESH_TOKEN_IN_COOKIE));
    }

    private void validateRefreshToken(String refreshToken) {
        JwtExceptionType jwtException = jwtTokenProvider.validateToken(refreshToken);
        if (jwtException == JwtExceptionType.EMPTY_JWT) {
            throw new UnAuthorizedException(ERR_UNAUTORIZED);
        }
        if (jwtException == JwtExceptionType.EXPIRED_JWT_TOKEN) {
            throw new UnAuthorizedException(ERR_REFRESH_TOKEN_EXPIRED);
        }
    }

    private void validateAccessToken(String accessToken) {
        if (accessToken != null) {
            JwtExceptionType jwtException = jwtTokenProvider.validateToken(accessToken);
            if (jwtException == JwtExceptionType.VALID_JWT_TOKEN) {
                setAuthentication(accessToken);
            } else if (jwtException == JwtExceptionType.EXPIRED_JWT_TOKEN) {
                throw new UnAuthorizedException(ERR_ACCESS_TOKEN_EXPIRED);
            }
        }
    }

    private void setAuthentication(String token) {
        Claims claims = jwtTokenProvider.getAccessTokenPayload(token);
        Authentication authentication = new UserAuthentication(Long.valueOf(String.valueOf(claims.get("id"))), null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
