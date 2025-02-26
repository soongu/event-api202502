package com.study.event.jwt.dto;

import com.study.event.domain.eventUser.entity.Role;
import lombok.Builder;

// 토큰에서 꺼낸 인증된 회원의 정보를 담은 DTO
@Builder
public record TokenUserInfo(
        Long userId
        , String email
        , Role role
) {
}
