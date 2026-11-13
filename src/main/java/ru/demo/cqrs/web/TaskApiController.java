package ru.demo.cqrs.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.demo.cqrs.command.CompleteTaskCommand;
import ru.demo.cqrs.command.CreateTaskCommand;
import ru.demo.cqrs.command.TaskCommandHandler;
import ru.demo.cqrs.domain.TaskNotFoundException;
import ru.demo.cqrs.eventstore.EventStore;
import ru.demo.cqrs.eventstore.StoredEventEntity;
import ru.demo.cqrs.query.TaskQueryService;
import ru.demo.cqrs.readmodel.StoredEventDto;
import ru.demo.cqrs.readmodel.TaskProjectionService;
import ru.demo.cqrs.readmodel.TaskViewEntity;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class TaskApiController {
    private final TaskCommandHandler commands;
    private final TaskQueryService queries;
    private final EventStore eventStore;
    private final TaskProjectionService projection;

    public TaskApiController(TaskCommandHandler commands, TaskQueryService queries,
                             EventStore eventStore, TaskProjectionService projection) {
        this.commands = commands;
        this.queries = queries;
        this.eventStore = eventStore;
        this.projection = projection;
    }

    @PostMapping("/commands/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, String> create(@RequestBody CreateTaskCommand command) {
        return Map.of("id", commands.handle(command));
    }

    @PostMapping("/commands/tasks/{id}/complete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void complete(@PathVariable String id) { commands.handle(new CompleteTaskCommand(id)); }

    @GetMapping("/queries/tasks")
    public List<TaskViewEntity> list() { return queries.findAll(); }

    @GetMapping("/queries/tasks/{id}")
    public TaskViewEntity get(@PathVariable String id) {
        TaskViewEntity task = queries.findById(id);
        if (task == null) throw new TaskNotFoundException(id);
        return task;
    }

    @GetMapping("/admin/events")
    public List<StoredEventDto> events() {
        return eventStore.loadAll().stream().map(this::toDto).toList();
    }

    @PostMapping("/admin/projections/tasks/rebuild")
    public Map<String, Integer> rebuild() {
        List<StoredEventDto> events = eventStore.loadAll().stream().map(this::toDto).toList();
        projection.rebuildFromEvents(events);
        return Map.of("replayedEvents", events.size());
    }

    private StoredEventDto toDto(StoredEventEntity entity) {
        try {
            var json = new com.fasterxml.jackson.databind.ObjectMapper().readTree(entity.getPayload());
            return new StoredEventDto(entity.getAggregateId(), entity.getEventType(),
                    json.has("title") ? json.get("title").asText() : null, entity.getOccurredAt());
        } catch (Exception exception) {
            throw new IllegalStateException("Invalid event payload", exception);
        }
    }
}
