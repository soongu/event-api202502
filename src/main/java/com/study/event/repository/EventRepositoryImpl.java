package com.study.event.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.event.domain.event.entity.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static com.study.event.domain.event.entity.QEvent.event;

@RequiredArgsConstructor
@Slf4j
public class EventRepositoryImpl implements EventRepositoryCustom {

    private final JPAQueryFactory factory;

    @Override
    public Slice<Event> findEvents(String sort, Pageable pageable, Long userId) {

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

        // 무한스크롤링을 위해서는 사이즈보다 1개 더 많이 조회해봐야 함
        List<Event> eventList = factory
                .selectFrom(event)
                .where(event.eventUser.id.eq(userId))
                .orderBy(orderSpecifier)
                .limit(pageable.getPageSize() + 1)
                .offset(pageable.getOffset())
                .fetch()
                ;

        // 추가 데이터가 있는지 확인
        boolean hasNext = false;
        //   기존데이터보다 1개 더 조회한결과  //  원래 조회하고 싶었던 결과수
        if (eventList.size() > pageable.getPageSize()) {
            hasNext = true;
            eventList.remove(eventList.size() - 1);
        }


        return new SliceImpl<>(eventList, pageable, hasNext);

    }

    @Override
    public Optional<Long> countEventByUser(Long userId) {
        return Optional.ofNullable(
                factory
                .select(event.count())
                .from(event)
                .where(event.eventUser.id.eq(userId))
                .fetchFirst()
        );
    }
}
