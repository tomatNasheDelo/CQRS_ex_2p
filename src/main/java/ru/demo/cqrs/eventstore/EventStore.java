package ru.demo.cqrs.eventstore;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Component
public class EventStore {
    private final StoredEventRepository repository;
    private final ObjectMapper objectMapper;

    public EventStore(StoredEventRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public StoredEventEntity append(String aggregateId, Object event, Instant occurredAt) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            return repository.save(new StoredEventEntity(
                    aggregateId, event.getClass().getSimpleName(), payload, occurredAt));
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Cannot serialize event", exception);
        }
    }

    @Transactional(readOnly = true)
    public List<StoredEventEntity> loadStream(String aggregateId) {
        return repository.findByAggregateIdOrderByIdAsc(aggregateId);
    }

    @Transactional(readOnly = true)
    public List<StoredEventEntity> loadAll() {
        return repository.findAll();
    }
}
