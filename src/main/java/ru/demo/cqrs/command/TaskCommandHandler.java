package ru.demo.cqrs.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.demo.cqrs.domain.TaskCompletedEvent;
import ru.demo.cqrs.domain.TaskCreatedEvent;
import ru.demo.cqrs.domain.TaskNotFoundException;
import ru.demo.cqrs.eventstore.EventStore;
import ru.demo.cqrs.eventstore.StoredEventEntity;
import ru.demo.cqrs.readmodel.TaskProjectionService;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class TaskCommandHandler {
    private final EventStore eventStore;
    private final TaskProjectionService projection;

    public TaskCommandHandler(EventStore eventStore, TaskProjectionService projection) {
        this.eventStore = eventStore;
        this.projection = projection;
    }

    @Transactional
    public String handle(CreateTaskCommand command) {
        if (command.title() == null || command.title().isBlank()) {
            throw new IllegalArgumentException("title must not be blank");
        }
        String id = UUID.randomUUID().toString();
        Instant now = Instant.now();
        TaskCreatedEvent event = new TaskCreatedEvent(id, command.title().trim(), now);
        eventStore.append(id, event, now);
        projection.apply(event);
        return id;
    }

    @Transactional
    public void handle(CompleteTaskCommand command) {
        List<StoredEventEntity> stream = eventStore.loadStream(command.taskId());
        if (stream.isEmpty()) throw new TaskNotFoundException(command.taskId());
        boolean created = stream.stream().anyMatch(event -> event.getEventType().equals("TaskCreatedEvent"));
        boolean completed = stream.stream().anyMatch(event -> event.getEventType().equals("TaskCompletedEvent"));
        if (!created) throw new TaskNotFoundException(command.taskId());
        if (completed) throw new IllegalStateException("Task is already completed");

        Instant now = Instant.now();
        TaskCompletedEvent event = new TaskCompletedEvent(command.taskId(), now);
        eventStore.append(command.taskId(), event, now);
        projection.apply(event);
    }
}
