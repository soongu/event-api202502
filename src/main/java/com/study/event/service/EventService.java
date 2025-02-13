package com.study.event.service;

import com.study.event.domain.event.dto.request.EventCreate;
import com.study.event.domain.event.dto.response.EventResponse;
import com.study.event.domain.event.entity.Event;
import com.study.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional  // JPA에서는 필수
public class EventService {

    private final EventRepository eventRepository;

    // 전체조회
    public List<EventResponse> getEvents(String sort) {
        return eventRepository.findEvents(sort)
                .stream()
                .map(EventResponse::from)
                .toList()
                ;
    }

    // 이벤트 등록
    public void saveEvent(EventCreate dto) {
        eventRepository.save(dto.toEntity());
    }
}
