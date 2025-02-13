package com.study.event.domain.event.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.study.event.domain.event.entity.Event;

import java.time.LocalDate;

// 이벤트 등록시 사용할 DTO
public record EventCreate(
        String title,
        String desc, // 이벤트 내용
        String imageUrl, // 메인 썸네일이미지
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate beginDate
) {

    // DTO를 엔터티로 변경하는 편의 메서드
    public Event toEntity() {
        return Event.builder()
                .title(this.title)
                .description(this.desc)
                .image(this.imageUrl)
                .date(this.beginDate)
                .build();
    }
}
