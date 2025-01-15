package com.ssafy.side.api.fcm.dto;

import jakarta.validation.constraints.NotEmpty;

public record FcmTokenRequestDto(
        @NotEmpty String token
) {
}
