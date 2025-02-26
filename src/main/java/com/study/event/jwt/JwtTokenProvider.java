package com.study.event.jwt;


import com.study.event.domain.eventUser.entity.EventUser;
import com.study.event.domain.eventUser.entity.Role;
import com.study.event.jwt.dto.TokenUserInfo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

// 인증을 위한 토큰을 생성하여 발급하고
// 전송된 토큰의 위조 및 만료시간을 검사하는 역할
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;

    // 비밀키를 생성
    private SecretKey key;

    @PostConstruct
    public void init() {
        // Base64로 인코딩된 key를 디코딩 후, HMAC-SHA 알고리즘으로 다시 암호화
        this.key = Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(jwtProperties.getSecretKey())
        );
    }

    // 토큰 발급 로직
    // 액세스 토큰 생성 (사용자가 들고다닐 신분증) : 유효기간이 짧다.
    public String createAccessToken(EventUser eventUser) {
        return createToken(eventUser, jwtProperties.getAccessTokenValidityTime());
    }

    // 리프레시 토큰 생성 (서버가 보관할 신분증을 재발급하기위한 정보) : 유효기간이 비교적 김
    public String createRefreshToken(EventUser eventUser) {
        return createToken(eventUser, jwtProperties.getRefreshTokenValidityTime());
    }

    // 공통 토큰 생성 로직
    private String createToken(EventUser eventUser, long validityTime) {

        // 현재 시간
        Date now = new Date();

        // 만료시간
        Date validity = new Date(now.getTime() + validityTime);


        // 서명을 넣어야 함
        return Jwts.builder()
                // 토큰에 포함시킬 일반적인 정보가 아닌 커스텀 정보(이메일, 권한)는 클레임에 따로 세팅
                // 커스텀정보는 제일 먼저 세팅
                .setClaims(Map.of(
                        "email", eventUser.getEmail(),
                        "role", eventUser.getRole().toString()
                ))
                .setIssuer("Event API")  // 발급자 정보
                .setIssuedAt(now) // 발급시간
                .setExpiration(validity) // 만료시간
                .setSubject(eventUser.getId().toString()) // 이 토큰을 구별할 유일한 값
                .signWith(key) // 서명 포함
                .compact();
    }

    /**
     * 토큰이 유효한지 검증하는 메서드
     * @param token JWT 토큰
     * @return 토큰이 정상이면 true, 만료되었거나 위조되었다면 false
     */
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("invalid token: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 검증된 토큰에서 이메일, 권한 등을 추출하는 메서드
     * @param token - 인증 토큰
     * @return 토큰에서 추출한 이메일, 권한등을 담은 DTO
     */
    public TokenUserInfo getCurrentLoginUserInfo(String token) {
        // 토큰에 들어있는 데이터들의 집합
        Claims claims = parseClaims(token);

        return TokenUserInfo.builder()
                .userId(Long.valueOf(claims.getSubject()))
                .email(claims.get("email", String.class))
                .role(Role.valueOf(claims.get("role", String.class)))
                .build();
    }

    /**
     * 내부적으로 토큰을 파싱하여 Claims 객체를 반환하는 메서드입니다.
     *
     * @param token JWT 토큰
     * @return 파싱된 Claims 객체
     * @throws JwtException 토큰이 유효하지 않은 경우 발생
     */
    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

}

