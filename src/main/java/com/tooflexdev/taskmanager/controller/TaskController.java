package com.tooflexdev.taskmanager.controller;

import com.tooflexdev.taskmanager.domain.Task;
import com.tooflexdev.taskmanager.domain.TaskStatus;
import com.tooflexdev.taskmanager.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/tasks")
@Tag(name = "Task Management", description = "Endpoints for managing tasks")
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    @Operation(summary = "Get all tasks", description = "Retrieve a list of all tasks")
    public List<Task> getAllTasks() {
        return taskService.getAllTasks();
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get tasks by status", description = "Retrieve tasks filtered by their status")
    public List<Task> getTasksByStatus(
            @Parameter(description = "The status of the tasks (e.g., PENDING, COMPLETED)")
            @PathVariable TaskStatus status) {
        return taskService.getTasksByStatus(status);
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Get tasks by category", description = "Retrieve tasks filtered by their category")
    public List<Task> getTasksByCategory(
            @Parameter(description = "The category of the tasks")
            @PathVariable String category) {
        return taskService.getTasksByCategory(category);
    }

    @PostMapping
    @Operation(summary = "Create a new task", description = "Create a new task with the provided details")
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        Task createdTask = taskService.createTask(task);
        return ResponseEntity.ok(createdTask);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a task", description = "Update an existing task by its ID")
    public ResponseEntity<Task> updateTask(
            @Parameter(description = "The ID of the task to update")
            @PathVariable Long id,
            @RequestBody Task updatedTask) {
        return taskService.updateTask(id, updatedTask)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a task", description = "Delete a task by its ID")
    public ResponseEntity<Void> deleteTask(
            @Parameter(description = "The ID of the task to delete")
            @PathVariable Long id) {
        if (taskService.deleteTask(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}