package com.study.event.controller;

import com.study.event.domain.eventUser.dto.request.LoginRequest;
import com.study.event.domain.eventUser.dto.request.SignupRequest;
import com.study.event.exception.LoginFailException;
import com.study.event.jwt.dto.TokenUserInfo;
import com.study.event.service.EventUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Slf4j
@RequiredArgsConstructor
public class AuthController {

    private final EventUserService eventUserService;

    // email 중복확인 API 생성
    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmail(String email) {
        boolean isDuplicate = eventUserService.checkEmailDuplicate(email);
        String message = isDuplicate ? "이메일이 중복되었습니다." : "사용 가능한 이메일입니다.";

        return ResponseEntity.ok().body(Map.of(
                "isDuplicate", isDuplicate,
                "message", message
        ));
    }

    // 인증 코드 검증 API
    @GetMapping("/code")
    public ResponseEntity<?> verifyCode(String email, String code) {
        log.info("{}'s verify code is [ {} ]", email, code);

        boolean isMatch = eventUserService.isMatchCode(email, code);

        log.info("code matches? - {}", isMatch);

        return ResponseEntity.ok().body(Map.of(
                "isMatch", isMatch
        ));
    }

    // 회원가입 마무리 요청
    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody SignupRequest dto) {

        log.info("save request user info - {}", dto);

        eventUserService.confirmSignup(dto);

        return ResponseEntity.ok().body(Map.of(
                "message", "회원가입이 완료되었습니다."
        ));
    }

    // 로그인 검증 요청
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest dto) {

        try {
            Map<String, Object> loginMap = eventUserService.authenticate(dto);

            return ResponseEntity.ok().body(loginMap);
        } catch (LoginFailException e) {
            return ResponseEntity.status(422)
                    .body(Map.of(
                            "message", e.getMessage()
                    ));
        }
    }

    // Premium회원으로 등급업 하는 요청
    @PutMapping("/promote")
    public ResponseEntity<?> promote(
            @AuthenticationPrincipal TokenUserInfo userInfo
    ) {

        Map<String, Object> responseMap = eventUserService.promoteToPremium(userInfo);

        return ResponseEntity.ok().body(responseMap);
    }

}
