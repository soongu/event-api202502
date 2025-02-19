package com.study.event.repository;

import com.study.event.domain.event.entity.Event;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;


// querydsl을 위한 custom repo
public interface EventRepositoryCustom {

    // 이벤트 목록 조회 + 페이징 처리
    Slice<Event> findEvents(String sort, Pageable pageable);

    // ...
}
