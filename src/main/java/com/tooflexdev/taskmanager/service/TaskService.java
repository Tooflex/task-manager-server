package com.tooflexdev.taskmanager.service;

import com.tooflexdev.taskmanager.domain.Task;
import com.tooflexdev.taskmanager.domain.TaskStatus;
import com.tooflexdev.taskmanager.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    // Get all tasks
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    // Get tasks by status
    public List<Task> getTasksByStatus(TaskStatus status) {
        return taskRepository.findByStatus(status);
    }

    // Get tasks by category
    public List<Task> getTasksByCategory(String category) {
        return taskRepository.findByCategory(category);
    }

    // Get tasks for a specific user
    public List<Task> getTasksByUser(Long userId) {
        return taskRepository.findByUserId(userId);
    }

    // Get tasks by priority
    public List<Task> getTasksByPriority(Integer priority) {
        return taskRepository.findByPriority(priority);
    }

    // Get tasks with overdue deadlines
    public List<Task> getOverdueTasks() {
        List<Task> tasks = taskRepository.findAll();
        return tasks.stream()
                .filter(task -> task.getDueDate().isBefore(LocalDateTime.now()))
                .collect(Collectors.toList());
    }

    // Get tasks created after a specific time
    public List<Task> getTasksCreatedAfter(LocalDateTime createdAt) {
        return taskRepository.findAll().stream()
                .filter(task -> task.getCreatedAt().isAfter(createdAt))
                .collect(Collectors.toList());
    }

    // Create a new task
    public Task createTask(Task task) {
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        return taskRepository.save(task);
    }

    // Update an existing task
    public Optional<Task> updateTask(Long taskId, Task updatedTask) {
        return taskRepository.findById(taskId).map(task -> {
            task.setTitle(updatedTask.getTitle());
            task.setDescription(updatedTask.getDescription());
            task.setStatus(updatedTask.getStatus());
            task.setCategory(updatedTask.getCategory());
            task.setPriority(updatedTask.getPriority());
            task.setDueDate(updatedTask.getDueDate());
            task.setUpdatedAt(LocalDateTime.now());
            return taskRepository.save(task);
        });
    }

    // Delete a task by ID
    public boolean deleteTask(Long taskId) {
        if (taskRepository.existsById(taskId)) {
            taskRepository.deleteById(taskId);
            return true;
        }
        return false;
    }
}
