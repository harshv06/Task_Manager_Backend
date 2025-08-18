package com.consultadd.taskmanager.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_task_mappings")
@Data
@Builder
@NoArgsConstructor @AllArgsConstructor
@IdClass(TaskMappingId.class)
public class TaskMapping {
    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;

    @ManyToOne
    @JoinColumn(name = "status_id")
    private Status status;

    @CreationTimestamp
    private LocalDateTime assignedAt;
}
