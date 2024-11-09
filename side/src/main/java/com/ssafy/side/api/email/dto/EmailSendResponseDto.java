package com.ssafy.side.api.email.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record EmailSendResponseDto(
        @JsonProperty("isOk") boolean isOk
) {
}
