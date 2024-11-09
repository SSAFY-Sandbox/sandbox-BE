package com.ssafy.side.api.email.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EmailAuthenticationRequestDto(
        @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.(com|org|net)$",
                message = "허용되지 않는 이메일 도메인입니다.")
        @NotBlank(message = "이메일 값은 null일 수 없습니다.")
        String email,
        @Size(min = 6, max = 6, message = "인증 코드는 6자리여야 합니다.")
        @Pattern(regexp = "^[A-Za-z0-9]{6}$", message = "인증 코드는 소문자, 대문자, 숫자로만 이루어져야 합니다.")
        @NotBlank(message = "인증 코드 값은 null일 수 없습니다.")
        String authenticationCode
) {
}
