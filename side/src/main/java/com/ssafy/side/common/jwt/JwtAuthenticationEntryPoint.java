package com.ssafy.side.common.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.side.common.exception.ErrorMessage;
import com.ssafy.side.common.exception.FailResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

  private final ObjectMapper mapper = new ObjectMapper();

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException authException) throws IOException {
    setResponse(response, HttpStatus.UNAUTHORIZED, authException.getMessage());
  }

  public void setResponse(HttpServletResponse response, HttpStatus status, String code) throws IOException {
    response.setContentType("application/json;charset=UTF-8");
    response.setStatus(status.value());

    FailResponse apiResponse = FailResponse.fail(status.value(), code);
    response.getWriter().println(mapper.writeValueAsString(apiResponse));
  }

}
