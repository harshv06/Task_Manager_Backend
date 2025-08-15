package com.consultadd.taskmanager.controller;


import com.consultadd.taskmanager.config.Utils;
import com.consultadd.taskmanager.dto.TaskReponseDTO;
import com.consultadd.taskmanager.dto.TaskRequestDTO;
import com.consultadd.taskmanager.dto.TaskResponseStatusCodeDTO;
import com.consultadd.taskmanager.model.Task;
import com.consultadd.taskmanager.model.User;
import com.consultadd.taskmanager.service.TaskService;
import com.consultadd.taskmanager.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final UserService userService;
    @PostMapping("/create")
    public ResponseEntity<TaskResponseStatusCodeDTO> createTask(@Valid @RequestBody TaskRequestDTO dto) {
        String email = Utils.getCurrentUserEmail();
        User user = userService.findByEmail(email).orElseThrow();

        Task created = taskService.createTask(dto, email);
        return ResponseEntity.ok(convertToWithStatusDto(created, user));
    }

    @GetMapping
    public ResponseEntity<List<TaskReponseDTO>> listTasks() {
        String email = Utils.getCurrentUserEmail();
        User user = userService.findByEmail(email).orElseThrow();

        return ResponseEntity.ok(
                taskService.getAllTasksById(user.getId())
                        .stream()
                        .map(this::convertToDto)
                        .collect(Collectors.toList())
        );
    }

    private TaskResponseStatusCodeDTO convertToWithStatusDto(Task task, User user) {

        return TaskResponseStatusCodeDTO.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .priority(task.getPriority().name())
                .deadline(task.getDeadline())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }

    private TaskReponseDTO convertToDto(Task task) {
        return TaskReponseDTO.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .priority(task.getPriority())
                .deadline(task.getDeadline())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .createdBy(task.getCreatedBy().getEmail())
                .build();
    }
}
