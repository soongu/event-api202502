package com.study.event.service;

import com.study.event.domain.event.dto.request.EventCreate;
import com.study.event.domain.event.dto.response.EventDetailResponse;
import com.study.event.domain.event.dto.response.EventResponse;
import com.study.event.domain.event.entity.Event;
import com.study.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional  // JPA에서는 필수
public class EventService {

    private final EventRepository eventRepository;

    // 전체조회
    @Transactional(readOnly = true)
    public Map<String, Object> getEvents(String sort, int pageNo) {

        Slice<Event> events = eventRepository.findEvents(
                sort,
                PageRequest.of(pageNo - 1, 4)
        );

        List<EventResponse> eventList = events.getContent()
                .stream()
                .map(EventResponse::from)
                .toList()
                ;

        return Map.of(
                "hasNext", events.hasNext()
                , "eventList", eventList
        );
    }

    // 단일 조회
    @Transactional(readOnly = true)
    public EventDetailResponse findOne(Long id) {

        Event event = eventRepository.findById(id).orElseThrow();

        return EventDetailResponse.from(event);
    }

    // 이벤트 등록
    public void saveEvent(EventCreate dto) {
        eventRepository.save(dto.toEntity());
    }

    // 이벤트 삭제
    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    // 이벤트 수정
    public void modifyEvent(EventCreate dto, Long id) {
        // JPA 수정은 수정메서드가 따로 있는게 아니라
        // 단일 조회 수행 후 엔터티 데이터를 변경하고 다시 save한다.
        Event foundEvent = eventRepository.findById(id).orElseThrow();
        foundEvent.changeEvent(dto);

        eventRepository.save(foundEvent);
    }
}
