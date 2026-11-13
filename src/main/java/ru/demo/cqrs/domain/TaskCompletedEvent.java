package ru.demo.cqrs.domain;

import java.time.Instant;

public record TaskCompletedEvent(String taskId, Instant occurredAt) { }
