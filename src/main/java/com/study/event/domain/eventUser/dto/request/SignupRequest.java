package com.study.event.domain.eventUser.dto.request;

import lombok.Builder;

@Builder
public record SignupRequest(
        String email,
        String password
) {


}

