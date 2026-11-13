package ru.demo.cqrs.readmodel;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskViewRepository extends JpaRepository<TaskViewEntity, String> {
    List<TaskViewEntity> findAllByOrderByCreatedAtDesc();
}
