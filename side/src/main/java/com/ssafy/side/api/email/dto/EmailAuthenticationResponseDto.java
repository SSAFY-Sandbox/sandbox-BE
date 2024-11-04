package com.ssafy.side.api.email.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record EmailAuthenticationResponseDto(
        @JsonProperty("isSuccess") boolean isSuccess
) {
}
