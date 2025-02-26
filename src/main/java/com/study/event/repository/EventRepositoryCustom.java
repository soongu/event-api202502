package com.study.event.repository;

import com.study.event.domain.event.entity.Event;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.Optional;


// querydsl을 위한 custom repo
public interface EventRepositoryCustom {

    // 이벤트 목록 조회 + 페이징 처리
    Slice<Event> findEvents(String sort, Pageable pageable, Long userId);

    // 특정 회원이 작성한 총 이벤트의 개수를 조회
    Optional<Long> countEventByUser(Long userId);

    // ...
}
