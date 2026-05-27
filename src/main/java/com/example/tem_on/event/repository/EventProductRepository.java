package com.example.tem_on.event.repository;

import com.example.tem_on.event.domain.entity.EventProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EventProductRepository extends JpaRepository<EventProductEntity, Long> {

    @Query("SELECT ep FROM EventProductEntity ep JOIN FETCH ep.event")
    List<EventProductEntity> findAllWithEvent();

    @Query("SELECT ep FROM EventProductEntity ep JOIN FETCH ep.event WHERE ep.id = :id")
    Optional<EventProductEntity> findByIdWithEvent(@Param("id") Long id);

    @Query("SELECT ep FROM EventProductEntity ep JOIN FETCH ep.event WHERE ep.event.id = :eventId")
    List<EventProductEntity> findByEventId(@Param("eventId") Long eventId);
}