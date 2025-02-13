package com.study.event.repository;

import com.study.event.domain.event.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository
        extends JpaRepository<Event, Long>, EventRepositoryCustom {

}
