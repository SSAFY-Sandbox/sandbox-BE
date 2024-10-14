package com.ssafy.side.common.Filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE) // 필터 우선순위 설정
public class HttpsRedirectFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 초기화 필요 시 구현
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // HTTP 요청인 경우 HTTPS로 리디렉션
        if (httpRequest.getScheme().equals("http")) {
            String httpsUrl = "https://" + httpRequest.getServerName() + httpRequest.getRequestURI();
            if (httpRequest.getQueryString() != null) {
                httpsUrl += "?" + httpRequest.getQueryString();
            }
            httpResponse.setStatus(HttpServletResponse.SC_TEMPORARY_REDIRECT); // 307 리디렉션
            httpResponse.setHeader("Location", httpsUrl);
        } else {
            // HTTPS 요청이면 필터 체인 계속 진행
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
        // 종료 작업 필요 시 구현
    }
}
