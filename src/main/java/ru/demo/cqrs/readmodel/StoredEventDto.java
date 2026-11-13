package ru.demo.cqrs.readmodel;

import java.time.Instant;

public record StoredEventDto(String aggregateId, String eventType, String title, Instant occurredAt) { }
