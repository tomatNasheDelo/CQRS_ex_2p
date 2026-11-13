package ru.demo.cqrs;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.demo.cqrs.command.CompleteTaskCommand;
import ru.demo.cqrs.command.CreateTaskCommand;
import ru.demo.cqrs.command.TaskCommandHandler;
import ru.demo.cqrs.eventstore.EventStore;
import ru.demo.cqrs.readmodel.TaskProjectionService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class CqrsEventStoreIntegrationTest {
    @Autowired TaskCommandHandler commands;
    @Autowired EventStore eventStore;
    @Autowired TaskProjectionService projection;

    @Test
    void persistsEventsUpdatesReadModelAndCanRebuildProjection() {
        String id = commands.handle(new CreateTaskCommand("Event sourced task"));
        commands.handle(new CompleteTaskCommand(id));

        assertEquals(2, eventStore.loadStream(id).size());
        assertTrue(projection.findById(id).isCompleted());

        projection.rebuildFromEvents(eventStore.loadAll().stream().map(event -> {
            try {
                var json = new com.fasterxml.jackson.databind.ObjectMapper().readTree(event.getPayload());
                return new ru.demo.cqrs.readmodel.StoredEventDto(event.getAggregateId(), event.getEventType(),
                        json.has("title") ? json.get("title").asText() : null, event.getOccurredAt());
            } catch (Exception exception) { throw new RuntimeException(exception); }
        }).toList());
        assertNotNull(projection.findById(id));
        assertTrue(projection.findById(id).isCompleted());
    }

    @Test
    void createCommandProducesCreatedEventAndReadProjection() {
        String id = commands.handle(new CreateTaskCommand("Read model"));
        assertEquals(1, eventStore.loadStream(id).size());
        assertFalse(projection.findById(id).isCompleted());
    }
}
