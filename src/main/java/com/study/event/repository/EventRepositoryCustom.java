package com.study.event.repository;

import com.study.event.domain.event.entity.Event;

import java.util.List;

// querydsl을 위한 custom repo
public interface EventRepositoryCustom {

    // 이벤트 목록 조회
    List<Event> findEvents(String sort);

    // ...
}
