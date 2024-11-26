package com.tooflexdev.taskmanager.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Getter
    private String title;
    @Setter
    @Getter
    private String description;

    @Setter
    @Getter
    @Enumerated(EnumType.STRING) // Maps the enum to a database column as a String
    private TaskStatus status;

    @Setter
    @Getter
    private String category;
    @Setter
    @Getter
    private Integer priority;
    @Setter
    @Getter
    private LocalDateTime dueDate;
    @Setter
    @Getter
    private LocalDateTime createdAt;
    @Setter
    @Getter
    private LocalDateTime updatedAt;

    @Setter
    @Getter
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "id")
    private User user;

    @OneToMany(mappedBy = "parentTask", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> subTasks;

    @ManyToOne
    @JoinColumn(name = "parent_task_id")
    private Task parentTask;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}
