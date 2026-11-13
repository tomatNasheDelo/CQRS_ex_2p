package ru.demo.cqrs.eventstore;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoredEventRepository extends JpaRepository<StoredEventEntity, Long> {
    List<StoredEventEntity> findByAggregateIdOrderByIdAsc(String aggregateId);
}
