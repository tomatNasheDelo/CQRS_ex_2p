package ru.demo.cqrs.domain;

public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException(String taskId) { super("Task not found: " + taskId); }
}
