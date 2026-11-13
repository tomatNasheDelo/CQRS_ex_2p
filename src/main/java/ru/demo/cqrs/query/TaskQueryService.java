package ru.demo.cqrs.query;

import org.springframework.stereotype.Service;
import ru.demo.cqrs.readmodel.TaskProjectionService;
import ru.demo.cqrs.readmodel.TaskViewEntity;

import java.util.List;

@Service
public class TaskQueryService {
    private final TaskProjectionService projection;

    public TaskQueryService(TaskProjectionService projection) { this.projection = projection; }
    public List<TaskViewEntity> findAll() { return projection.findAll(); }
    public TaskViewEntity findById(String id) { return projection.findById(id); }
}
