package com.tooflexdev.taskmanager.repository;

import com.tooflexdev.taskmanager.domain.Task;
import com.tooflexdev.taskmanager.domain.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // Find tasks by status
    List<Task> findByStatus(TaskStatus status);

    // Find tasks by category
    List<Task> findByCategory(String category);

    // Find tasks by user ID
    List<Task> findByUserId(Long userId);

    // Find tasks by priority
    List<Task> findByPriority(Integer priority);

}