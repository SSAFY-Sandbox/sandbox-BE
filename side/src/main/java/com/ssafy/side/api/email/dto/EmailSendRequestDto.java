package com.ssafy.side.api.email.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record EmailSendRequestDto(
        @NotBlank(message = "이메일 값은 null일 수 없습니다.")
        @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.(com|org|net)$",
                message = "허용되지 않는 이메일 도메인입니다.")
        String email
) {
}
