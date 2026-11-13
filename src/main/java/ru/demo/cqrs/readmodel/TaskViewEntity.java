package ru.demo.cqrs.readmodel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "task_read_model")
public class TaskViewEntity {
    @Id
    private String id;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private boolean completed;
    @Column(nullable = false)
    private Instant createdAt;
    private Instant completedAt;

    protected TaskViewEntity() { }

    public TaskViewEntity(String id, String title, boolean completed, Instant createdAt, Instant completedAt) {
        this.id = id;
        this.title = title;
        this.completed = completed;
        this.createdAt = createdAt;
        this.completedAt = completedAt;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public boolean isCompleted() { return completed; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getCompletedAt() { return completedAt; }
}
