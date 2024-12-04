package com.tooflexdev.taskmanager.service;

import com.tooflexdev.taskmanager.domain.Task;
import com.tooflexdev.taskmanager.domain.TaskStatus;
import com.tooflexdev.taskmanager.domain.User;
import com.tooflexdev.taskmanager.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class TaskServiceTests {

    @Mock
    private TaskRepository taskRepository;

    private TaskService taskService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        taskService = new TaskService(taskRepository);
    }

    @Test
    void testGetAllTasks() {
        Task mockTask = new Task();
        mockTask.setTitle("Test Task");
        mockTask.setDescription("This is a test task");

        Task mockTask2 = new Task();
        mockTask2.setTitle("Test Task 2");
        mockTask2.setDescription("This is a test task 2");

        taskRepository.save(mockTask);
        taskRepository.save(mockTask2);

        when(taskRepository.findAll()).thenReturn(List.of(mockTask, mockTask2));

        // Check we get all tasks
        assertEquals(2, taskService.getAllTasks().size());
    }

    @Test
    void testGetTasksByInProgressStatus() {
        Task mockInProgressTask = new Task();
        mockInProgressTask.setTitle("Test Task");
        mockInProgressTask.setDescription("This is a in progress test task");
        mockInProgressTask.setStatus(TaskStatus.IN_PROGRESS);

        Task mockDoneTask = new Task();
        mockDoneTask.setTitle("Done Task");
        mockDoneTask.setDescription("This is a done test task");
        mockDoneTask.setStatus(TaskStatus.DONE);

        taskRepository.save(mockInProgressTask);
        taskRepository.save(mockDoneTask);

        when(taskRepository.findByStatus(TaskStatus.IN_PROGRESS)).thenReturn(Collections.singletonList(mockInProgressTask));
        // Check we get the only pending task
        assertEquals(1, taskService.getTasksByStatus(TaskStatus.IN_PROGRESS).size());
        // Check title of the task
        assertEquals("Test Task", taskService.getTasksByStatus(TaskStatus.IN_PROGRESS).get(0).getTitle());
        // Check description of the task
        assertEquals("This is a in progress test task", taskService.getTasksByStatus(TaskStatus.IN_PROGRESS).get(0).getDescription());
    }

    @Test
    void testGetTasksByPendingStatus() {
        Task mockPendingTask = new Task();
        mockPendingTask.setTitle("Test Task");
        mockPendingTask.setDescription("This is a pending test task");
        mockPendingTask.setStatus(TaskStatus.PENDING);

        Task mockDoneTask = new Task();
        mockDoneTask.setTitle("Done Task");
        mockDoneTask.setDescription("This is a done test task");
        mockDoneTask.setStatus(TaskStatus.DONE);

        taskRepository.save(mockPendingTask);
        taskRepository.save(mockDoneTask);

        when(taskRepository.findByStatus(TaskStatus.PENDING)).thenReturn(Collections.singletonList(mockPendingTask));
        // Check we get the only pending task
        assertEquals(1, taskService.getTasksByStatus(TaskStatus.PENDING).size());
        // Check title of the task
        assertEquals("Test Task", taskService.getTasksByStatus(TaskStatus.PENDING).get(0).getTitle());
        // Check description of the task
        assertEquals("This is a pending test task", taskService.getTasksByStatus(TaskStatus.PENDING).get(0).getDescription());
    }

    @Test
    void testGetTasksByDoneStatus() {
        Task mockPendingTask = new Task();
        mockPendingTask.setTitle("Test Task");
        mockPendingTask.setDescription("This is a pending test task");
        mockPendingTask.setStatus(TaskStatus.PENDING);

        Task mockDoneTask = new Task();
        mockDoneTask.setTitle("Done Task");
        mockDoneTask.setDescription("This is a done test task");
        mockDoneTask.setStatus(TaskStatus.DONE);

        taskRepository.save(mockPendingTask);
        taskRepository.save(mockDoneTask);

        when(taskRepository.findByStatus(TaskStatus.DONE)).thenReturn(Collections.singletonList(mockDoneTask));
        // Check we get the only pending task
        assertEquals(1, taskService.getTasksByStatus(TaskStatus.DONE).size());
        // Check title of the task
        assertEquals("Done Task", taskService.getTasksByStatus(TaskStatus.DONE).get(0).getTitle());
        // Check description of the task
        assertEquals("This is a done test task", taskService.getTasksByStatus(TaskStatus.DONE).get(0).getDescription());
    }

    @Test
    void testGetTasksByCategory() {
        Task mockTask = new Task();
        mockTask.setTitle("Test Task");
        mockTask.setDescription("This is a test task");
        mockTask.setCategory("Test Category");

        Task mockTask2 = new Task();
        mockTask2.setTitle("Test Task 2");
        mockTask2.setDescription("This is a test task 2");
        mockTask2.setCategory("Test Category");

        taskRepository.save(mockTask);
        taskRepository.save(mockTask2);

        when(taskRepository.findByCategory("Test Category")).thenReturn(List.of(mockTask, mockTask2));

        // Check we get all tasks with the category "Test Category"
        assertEquals(2, taskService.getTasksByCategory("Test Category").size());
    }

    @Test
    void testGetTasksByUser() {

        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testUser");

        Task mockTask = new Task();
        mockTask.setTitle("Test Task");
        mockTask.setDescription("This is a test task");
        mockTask.setUser(mockUser);

        Task mockTask2 = new Task();
        mockTask2.setTitle("Test Task 2");
        mockTask2.setDescription("This is a test task 2");
        mockTask2.setUser(mockUser);

        taskRepository.save(mockTask);
        taskRepository.save(mockTask2);

        when(taskRepository.findByUserId(1L)).thenReturn(List.of(mockTask, mockTask2));

        // Check we get all tasks with the user ID 1
        assertEquals(2, taskService.getTasksByUser(1L).size());
    }

    @Test
    void testGetTasksByPriority() {
        Task mockTask = new Task();
        mockTask.setTitle("Test Task");
        mockTask.setDescription("This is a test task");
        mockTask.setPriority(1);

        Task mockTask2 = new Task();
        mockTask2.setTitle("Test Task 2");
        mockTask2.setDescription("This is a test task 2");
        mockTask2.setPriority(1);

        taskRepository.save(mockTask);
        taskRepository.save(mockTask2);

        when(taskRepository.findByPriority(1)).thenReturn(List.of(mockTask, mockTask2));

        // Check we get all tasks with the priority 1
        assertEquals(2, taskService.getTasksByPriority(1).size());
    }

    @Test
    void testGetOverdueTasks() {
        Task mockTask = new Task();
        mockTask.setTitle("Test Task");
        mockTask.setDescription("This is a test task");
        // Set the due date to yesterday
        mockTask.setDueDate(Instant.now().atZone(ZoneId.systemDefault()).toLocalDateTime().minusDays(1));

        Task mockTask2 = new Task();
        mockTask2.setTitle("Test Task 2");
        mockTask2.setDescription("This is a test task 2");
        // Set the due date to tomorrow
        mockTask2.setDueDate(Instant.now().atZone(ZoneId.systemDefault()).toLocalDateTime().plusDays(1));

        taskRepository.save(mockTask);
        taskRepository.save(mockTask2);

        // Mock the repository to return all tasks
        when(taskRepository.findAll()).thenReturn(List.of(mockTask, mockTask2));

        assertEquals(1, taskService.getOverdueTasks().size());

    }

    @Test
    void getTasksCreatedAfter() {

        LocalDateTime dateToCompare = Instant.now().atZone(ZoneId.systemDefault()).toLocalDateTime().minusDays(1);

        Task mockTask = new Task();
        mockTask.setTitle("Test Task");
        mockTask.setDescription("This is a test task");
        LocalDateTime dateBefore = dateToCompare.minusDays(2);
        mockTask.setCreatedAt(dateBefore);

        Task mockTask2 = new Task();
        mockTask2.setTitle("Test Task 2");
        mockTask2.setDescription("This is a test task 2");
        LocalDateTime dateAfter = dateToCompare.plusDays(2);
        mockTask2.setCreatedAt(dateAfter);

        taskRepository.save(mockTask);
        taskRepository.save(mockTask2);

        // Mock the repository to return all tasks
        when(taskRepository.findAll()).thenReturn(List.of(mockTask, mockTask2));
        System.out.println(dateToCompare);
        System.out.println(mockTask.getCreatedAt());
        System.out.println(mockTask2.getCreatedAt());


        assertEquals(1, taskService.getTasksCreatedAfter(dateToCompare).size());
    }
}
