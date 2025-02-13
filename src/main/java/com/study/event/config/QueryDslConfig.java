package com.study.event.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueryDslConfig {

    @PersistenceContext // JPA에서 주입
    private EntityManager em;

    @Bean
    public JPAQueryFactory factory() {
        return new JPAQueryFactory(em);
    }
}
