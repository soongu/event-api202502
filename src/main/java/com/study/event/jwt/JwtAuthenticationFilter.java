package com.study.event.jwt;

import com.study.event.domain.eventUser.entity.Role;
import com.study.event.jwt.dto.TokenUserInfo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// 토큰 검증만 수행 - 토큰이 없거나 위조되거나 만료되었으면 요청을 돌려보냄
@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;

    // 실제 필터링 로직을 수행하는 메서드
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // 사용자가 전달한 토큰을 가져와야 함.
        String token = resolveTokenFromHeader(request);

        // 토큰 유효성 검증 및 토큰이 유효하다면 스프링에게 유효하다는 정보를 전달
        validateAndAuthenticate(token);

        // 다음 필터로 넘어가기
        filterChain.doFilter(request, response);

    }

    /**
     * 토큰이 유효하면, SecurityContext에 Authentication을 세팅하는 메서드.
     *
     * @param token JWT 토큰 문자열
     */
    private void validateAndAuthenticate(String token) {

        log.debug("parsed token: {}", token);

        // 토큰이 존재하고, 유효성 검증에 통과하면 인증 처리
        if (StringUtils.hasText(token) && tokenProvider.validateToken(token)) {
            // 토큰이 유효하므로, 토큰에서 로그인한 사용자의 이메일, id, 권한 추출
            TokenUserInfo userInfo = tokenProvider.getCurrentLoginUserInfo(token);

            // 이 회원의 권한을 조회
            Role role = userInfo.role();

            // 회원의 권한을 담을 배열 생성
            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(role.toString()));

            // Spring Security에게 접근을 허용하라고 명령
            // Authentication 객체 생성 → SecurityContextHolder에 저장
            Authentication authentication =
                    /*
                        첫번째 파라미터: 인증된 사용자의 이메일, 권한, id 저장 - 컨트롤러들이 빼서 사용할 수 있음
                        두번째 파라미터: 비밀번호를 저장 (일반적으로 저장하지 않음)
                        세번째 파라미터: 권한정보를 저장 (나중에 인가 처리시 사용)
                     */
                    new UsernamePasswordAuthenticationToken(userInfo, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.info("authentication success: email - {}", userInfo.email());
            log.info("authentication success: role - {}", userInfo.role());
        }
    }

    /**
     * API 요청(헤더 기반)에서 토큰을 추출하는 메서드입니다.
     *
     * @param request HttpServletRequest
     * @return Bearer 토큰 문자열(앞의 "Bearer " 제거 후 반환)
     */
    private String resolveTokenFromHeader(HttpServletRequest request) {
        // Authorization 헤더에서 토큰 추출
        String bearerToken = request.getHeader("Authorization");
        // bearerToken이 "Bearer "로 시작하면 해당 부분을 제거하고 토큰만 반환
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        // 조건을 만족하지 못하면 null 반환
        return null;
    }


}
