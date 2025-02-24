package com.study.event.domain.eventUser.dto.request;

import lombok.Builder;

@Builder
public record LoginRequest(
        String email,
        String password
) {
}
