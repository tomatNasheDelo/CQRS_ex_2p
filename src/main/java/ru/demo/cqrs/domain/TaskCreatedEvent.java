package ru.demo.cqrs.domain;

import java.time.Instant;

public record TaskCreatedEvent(String taskId, String title, Instant occurredAt) { }
