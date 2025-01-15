package com.ssafy.side.api.fcm.dto;

import jakarta.validation.constraints.NotEmpty;

public record FcmTokenRequest(
        @NotEmpty String token
) {
}
