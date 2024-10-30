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
import java.util.Set;
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

    private static final Set<String> ISSUE_TOKEN_API_URL_SET = Set.of(
            "/oauth/reissue",
            "/oauth/cookie/reissue",
            "/oauth/authorization/reissue"
    );
    private static final Set<String> LOGOUT_API_URL_SET = Set.of(
            "/oauth/logout",
            "/oauth/cookie/logout",
            "/oauth/authorization/logout"
    );
    private static final String LOGOUT_WITH_HEADER_API_URL = "/oauth/logout/authorization";
    private static final String REISSUE_WITH_HEADER_API_URL = "/oauth/reissue/authorization";
    private static final String ACCESS_TOKEN_IN_COOKIE_URL = "/oauth/cookie/member";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        try {
            String accessToken;
            if (ACCESS_TOKEN_IN_COOKIE_URL.equals(request.getRequestURI())) {
                accessToken = getAccessTokenFromCookies(request);
            } else {
                accessToken = jwtTokenProvider.resolveAccessToken(request);
            }
            if (ISSUE_TOKEN_API_URL_SET.contains(request.getRequestURI())) {
                handleTokenReissue(request);
            } else if (LOGOUT_API_URL_SET.contains(request.getRequestURI())) {
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
        String refreshToken;
        if (REISSUE_WITH_HEADER_API_URL.equals(request.getRequestURI())) {
            refreshToken = jwtTokenProvider.resolveRefreshToken(request);
        } else {
            refreshToken = getRefreshTokenFromCookies(request);
        }
        validateRefreshToken(refreshToken);
        Long memberId = jwtTokenProvider.validateMemberRefreshToken(refreshToken);
        String newAccessToken = jwtTokenProvider.generateAccessToken(memberId);

        setAuthentication(newAccessToken);
        request.setAttribute("newAccessToken", newAccessToken);
    }

    private void handleLogout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken;
        if (LOGOUT_WITH_HEADER_API_URL.equals(request.getRequestURI())) {
            refreshToken = jwtTokenProvider.resolveRefreshToken(request);
        } else {
            refreshToken = getRefreshTokenFromCookies(request);
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    cookie.setMaxAge(0);
                    cookie.setValue(null);
                    cookie.setPath("/");
                    response.addCookie(cookie);
                }
            }
        }
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
                .orElseThrow(() -> new UnAuthorizedException(ERR_REFRESH_TOKEN_EXPIRED));
    }

    private String getAccessTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            throw new UnAuthorizedException(ERR_NO_COOKIE);
        }
        return Arrays.stream(cookies)
                .filter(cookie -> "accessToken".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElseThrow(() -> new UnAuthorizedException(ERR_ACCESS_TOKEN_EXPIRED));
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
