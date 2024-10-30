package com.ssafy.side.api.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record LoginAccessTokenDto(
        @NotNull(message = "accessToken 값은 null일 수 없습니다.") String accessToken,
        String refreshToken
) {
}

