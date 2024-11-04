package com.ssafy.side.api.email.dto;

import jakarta.validation.constraints.NotNull;

public record EmailSendRequestDto(
        @NotNull(message = "이메일 값은 null일 수 없습니다.") String email
) {
}
