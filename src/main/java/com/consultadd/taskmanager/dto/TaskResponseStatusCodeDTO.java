package com.consultadd.taskmanager.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
public class TaskResponseStatusCodeDTO {
    private Long id;
    private String title;
    private String description;
    private String priority;
    private LocalDateTime deadline;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Set<String> tagNames;
//    private Set<String> assigneeEmails;
    private String status;
}
