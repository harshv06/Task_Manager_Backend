package com.consultadd.taskmanager.dto;

import com.consultadd.taskmanager.model.Task;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskReponseDTO {
    private Long id;
    private String title;
    private String description;
    private Task.Priority priority;
    private LocalDateTime deadline;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private Set<String> tagNames;
    private Set<String> assignees;
}
