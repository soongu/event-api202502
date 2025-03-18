package com.study.event.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Slf4j
public class HealthCheckController {

    @GetMapping("/status")
    public ResponseEntity<?> healthCheck() {
        String checkMessage = "서버가 실행중입니다.....";
        log.info(checkMessage);
        return ResponseEntity.ok()
                .body(Map.of(
                        "message", checkMessage
                ));
    }
}
