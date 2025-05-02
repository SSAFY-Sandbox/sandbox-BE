package com.ssafy.side.api.fcm.dto;

import jakarta.validation.constraints.NotEmpty;

public record NotificationRequestDto(
        @NotEmpty
        String title,
        @NotEmpty
        String body
) {
}
