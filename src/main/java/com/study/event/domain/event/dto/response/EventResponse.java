package com.study.event.domain.event.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.study.event.domain.event.entity.Event;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record EventResponse(
        String eventId,
        String title,
        @JsonFormat(pattern = "yyyy년 MM월 dd일")
        LocalDate startDate,
        String imgUrl
) {

    // 엔터티를 DTO로 바꿔주는 편의 메서드
    public static EventResponse from(Event event) {
        return EventResponse.builder()
                .eventId(event.getId().toString())
                .imgUrl(event.getImage())
                .title(event.getTitle())
                .startDate(event.getDate())
                .build();
    }
}
