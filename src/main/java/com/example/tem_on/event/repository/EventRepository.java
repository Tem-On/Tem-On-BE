package com.example.tem_on.event.repository;


import com.example.tem_on.event.domain.entity.EventEntity;
import com.example.tem_on.event.domain.entity.EventStatus; 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<EventEntity, Long> {

    @Query("SELECT e FROM EventEntity e WHERE e.status = :status AND :now BETWEEN e.startAt AND e.endAt ORDER BY e.startAt ASC")
    List<EventEntity> findActiveEvents(@Param("now") LocalDateTime now, @Param("status") EventStatus status);

    @Query("SELECT e FROM EventEntity e WHERE e.status = :status AND e.startAt > :now ORDER BY e.startAt ASC")
    List<EventEntity> findUpcomingEvents(@Param("now") LocalDateTime now, @Param("status") EventStatus status);
}