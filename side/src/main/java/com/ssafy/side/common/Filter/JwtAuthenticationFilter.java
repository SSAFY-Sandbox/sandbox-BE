package com.ssafy.side.common.Filter;

import static com.ssafy.side.common.exception.ErrorMessage.ERR_ACCESS_TOKEN_EXPIRED;
import static com.ssafy.side.common.exception.ErrorMessage.ERR_NO_COOKIE;
import static com.ssafy.side.common.exception.ErrorMessage.ERR_NO_REFRESH_TOKEN_IN_COOKIE;
import static com.ssafy.side.common.exception.ErrorMessage.ERR_REFRESH_TOKEN_EXPIRED;
import static com.ssafy.side.common.exception.ErrorMessage.ERR_UNAUTORIZED;

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
import java.util.Optional;
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

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        try {
            String accessToken = jwtTokenProvider.resolveToken(request);
            /**
             *  토큰 재발급 로직
             */
            if (ISSUE_TOKEN_API_URL.equals(request.getRequestURI())) {
                Cookie[] cookies = request.getCookies();
                if (cookies == null) {
                    throw new UnAuthorizedException(ERR_NO_COOKIE);
                }
                Optional<String> refreshToken = Arrays.stream(cookies)
                        .filter(cookie -> "refreshToken".equals(cookie.getName()))
                        .map(Cookie::getValue)
                        .findFirst();

                if (refreshToken.isEmpty()) {
                    throw new UnAuthorizedException(ERR_NO_REFRESH_TOKEN_IN_COOKIE);
                }

                // 토큰이 없을 때
                if (jwtTokenProvider.validateToken(refreshToken.get()) == JwtExceptionType.EMPTY_JWT) {
                    throw new UnAuthorizedException(ERR_UNAUTORIZED);
                }

                // access, refresh 둘 다 만료
                if (jwtTokenProvider.validateToken(refreshToken.get()) == JwtExceptionType.EXPIRED_JWT_TOKEN) {
                    throw new UnAuthorizedException(ERR_REFRESH_TOKEN_EXPIRED);
                }

                // 토큰 재발급
                Long memberId = jwtTokenProvider.validateMemberRefreshToken(refreshToken.get());

                String newAccessToken = jwtTokenProvider.generateAccessToken(memberId);

                setAuthentication(newAccessToken);
                request.setAttribute("newAccessToken", newAccessToken);
            }

            /**
             * 토큰 검증 로직
             */
            else {
                if (accessToken != null) {
                    // 토큰 검증
                    JwtExceptionType jwtException = jwtTokenProvider.validateToken(accessToken);
                    if (jwtException == JwtExceptionType.VALID_JWT_TOKEN) {
                        setAuthentication(accessToken);
                    }
                    else if (jwtException == JwtExceptionType.EXPIRED_JWT_TOKEN){
                        throw new UnAuthorizedException(ERR_ACCESS_TOKEN_EXPIRED);
                    }
                }
            }
        } catch (UnAuthorizedException e) {
            jwtAuthenticationEntryPoint.setResponse(response, HttpStatus.UNAUTHORIZED, e.getCode().toString());
            return;
        }

        chain.doFilter(request, response);
    }

    private void setAuthentication(String token) {
        Claims claims = jwtTokenProvider.getAccessTokenPayload(token);
        Authentication authentication = new UserAuthentication(Long.valueOf(String.valueOf(claims.get("id"))), null,
                null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
