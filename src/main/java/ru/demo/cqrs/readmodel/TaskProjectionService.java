package ru.demo.cqrs.readmodel;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.demo.cqrs.domain.TaskCompletedEvent;
import ru.demo.cqrs.domain.TaskCreatedEvent;

import java.util.List;

@Service
public class TaskProjectionService {
    private final TaskViewRepository repository;

    public TaskProjectionService(TaskViewRepository repository) { this.repository = repository; }

    @Transactional
    public void apply(TaskCreatedEvent event) {
        repository.save(new TaskViewEntity(event.taskId(), event.title(), false, event.occurredAt(), null));
    }

    @Transactional
    public void apply(TaskCompletedEvent event) {
        repository.findById(event.taskId()).ifPresent(task -> {
            task = new TaskViewEntity(task.getId(), task.getTitle(), true, task.getCreatedAt(), event.occurredAt());
            repository.save(task);
        });
    }

    @Transactional(readOnly = true)
    public List<TaskViewEntity> findAll() { return repository.findAllByOrderByCreatedAtDesc(); }

    @Transactional(readOnly = true)
    public TaskViewEntity findById(String id) { return repository.findById(id).orElse(null); }

    @Transactional
    public void rebuildFromEvents(List<StoredEventDto> events) {
        repository.deleteAllInBatch();
        for (StoredEventDto event : events) {
            switch (event.eventType()) {
                case "TaskCreatedEvent" -> apply(new TaskCreatedEvent(
                        event.aggregateId(), event.title(), event.occurredAt()));
                case "TaskCompletedEvent" -> apply(new TaskCompletedEvent(
                        event.aggregateId(), event.occurredAt()));
                default -> throw new IllegalArgumentException("Unknown event type: " + event.eventType());
            }
        }
    }
}
