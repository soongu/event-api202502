package com.study.event.controller;

import com.study.event.domain.event.dto.request.EventCreate;
import com.study.event.domain.event.dto.response.EventDetailResponse;
import com.study.event.domain.event.dto.response.EventResponse;
import com.study.event.domain.event.entity.Event;
import com.study.event.jwt.dto.TokenUserInfo;
import com.study.event.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/events")
@Slf4j
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    // 전체조회 요청
    @GetMapping
    public ResponseEntity<?> getList(
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "1") int page,
            @AuthenticationPrincipal TokenUserInfo userInfo
            ) {
        if (sort == null) {
            return ResponseEntity.badRequest().body(
                    Map.of(
                            "message", "정렬 기준은 필수입니다."
                    )
            );
        }

        Map<String, Object> events = eventService.getEvents(sort, page, userInfo.userId());

        return ResponseEntity.ok().body(events);
    }

    // 등록 요청 - (어떤 유저가 등록한 이벤트인지 확인)
    @PostMapping
    public ResponseEntity<?> register(
            @RequestBody EventCreate dto,
            @AuthenticationPrincipal TokenUserInfo userInfo
    ) {
        eventService.saveEvent(dto, userInfo.email());

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

    // 삭제 요청
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvent(
            @PathVariable Long id
    ) {
        eventService.deleteEvent(id);

        return ResponseEntity.ok().body(Map.of(
                "message", "이벤트가 삭제되었습니다. id - " + id
        ));
    }
    // 수정 요청
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEvent(
            @PathVariable Long id
            , @RequestBody EventCreate dto
    ) {
        eventService.modifyEvent(dto, id);

        return ResponseEntity.ok().body(Map.of(
                "message", "이벤트가 수정되었습니다. id - " + id
        ));
    }
}
