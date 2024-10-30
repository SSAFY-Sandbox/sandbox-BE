package com.ssafy.side.api.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record LoginAccessTokenDto(
        @NotNull(message = "accessToken 값은 null일 수 없습니다.") String accessToken,
        Optional<String> refreshToken
) {
    public LoginAccessTokenDto(String accessToken, String refreshToken) {
        this(accessToken, Optional.ofNullable(refreshToken));
    }
}

