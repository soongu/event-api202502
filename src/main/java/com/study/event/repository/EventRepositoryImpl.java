package com.study.event.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.event.domain.event.entity.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static com.study.event.domain.event.entity.QEvent.event;

@RequiredArgsConstructor
@Slf4j
public class EventRepositoryImpl implements EventRepositoryCustom {

    private final JPAQueryFactory factory;

    @Override
    public List<Event> findEvents(String sort) {

        OrderSpecifier<?> orderSpecifier;
        switch (sort) {
            case "id":
                orderSpecifier = event.id.desc();
                break;
            case "title":
                orderSpecifier = event.title.asc();
                break;
            default:
                orderSpecifier = event.date.desc();
        }

        return factory
                .selectFrom(event)
                .orderBy(orderSpecifier)
                .fetch()
                ;

    }
}
