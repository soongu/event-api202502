package com.study.event.domain.event.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.study.event.domain.event.entity.Event;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record EventDetailResponse(
        String id,
        String title,
        String desc,
        @JsonProperty("img-url")
        String image,
        @JsonProperty("start-date")
        @JsonFormat(pattern = "yyyy년 MM월 dd일")
        LocalDate startDate
) {

    public static EventDetailResponse from(Event event) {
        return EventDetailResponse.builder()
                .id(event.getId().toString())
                .title(event.getTitle())
                .desc(event.getDescription())
                .image(event.getImage())
                .startDate(event.getDate())
                .build();
    }
}
