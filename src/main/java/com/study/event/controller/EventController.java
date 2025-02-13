package com.study.event.controller;

import com.study.event.domain.event.dto.request.EventCreate;
import com.study.event.domain.event.dto.response.EventDetailResponse;
import com.study.event.domain.event.dto.response.EventResponse;
import com.study.event.domain.event.entity.Event;
import com.study.event.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/events")
@Slf4j
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    // 전체조회 요청
    @GetMapping
    public ResponseEntity<?> getList(
            @RequestParam(defaultValue = "id") String sort
    ) {
        List<EventResponse> events = eventService.getEvents(sort);
        return ResponseEntity.ok().body(events);
    }

    // 등록 요청
    @PostMapping
    public ResponseEntity<?> register(@RequestBody EventCreate dto) {
        eventService.saveEvent(dto);

        return ResponseEntity.ok().body(Map.of(
                "message", "이벤트가 정상 등록되었습니다."
        ));
    }

    // 단일 조회 요청
    @GetMapping("/{eventId}")
    public ResponseEntity<?> getEvent(@PathVariable Long eventId) {

        if (eventId == null || eventId < 1) {
            String errorMessage = "eventId가 유효하지 않습니다.";
            log.warn(errorMessage);
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message", errorMessage
                    ));
        }

        EventDetailResponse detailResponse = eventService.findOne(eventId);

        return ResponseEntity.ok().body(detailResponse);
    }
}
