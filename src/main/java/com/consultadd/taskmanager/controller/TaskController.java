package com.consultadd.taskmanager.controller;


import com.consultadd.taskmanager.config.Utils;
import com.consultadd.taskmanager.dto.*;
import com.consultadd.taskmanager.model.Tag;
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
    public ResponseEntity<TaskReponseDTO> createTask(@Valid @RequestBody TaskRequestDTO dto) {
        String email = Utils.getCurrentUserEmail();
        User user = userService.findByEmail(email).orElseThrow();

        Task created = taskService.createTask(dto, email,dto.getTagNames());
        return ResponseEntity.ok(convertToDto(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskReponseDTO> updateTask(
            @PathVariable Long id,
            @RequestBody TaskRequestDTO dto) {
        Task updated = taskService.updateTask(id, dto,dto.getTagNames());
        return ResponseEntity.ok(convertToDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id){
        taskService.deleteTask(id);
        return ResponseEntity.ok("Task Deleted");
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

    @GetMapping("/{id}")
    public ResponseEntity<TaskReponseDTO> getTask(@PathVariable Long id) {
        Task task = taskService.getTaskById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        return ResponseEntity.ok(convertToDto(task));
    }

    @GetMapping("/with-status")
    public ResponseEntity<List<TaskResponseStatusCodeDTO>> listTasksWithStatus() {
        String email = Utils.getCurrentUserEmail();
        User user = userService.findByEmail(email).orElseThrow();

        return ResponseEntity.ok(
                taskService.getAllTasksById(user.getId())
                        .stream()
                        .map(task->convertToWithStatusDto(task,user.getId()))
                        .collect(Collectors.toList())
        );
    }


    @PostMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody UpdateStatusDTO dto) {
        String email = Utils.getCurrentUserEmail();
        User user = userService.findByEmail(email).orElseThrow();
        Long userId = user.getId();
        taskService.updateTaskStatus(id, userId, dto.getStatusId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/assign")
    public ResponseEntity<?> assignUsers(@PathVariable Long id, @RequestBody AssignRequestDTO dto) {
        taskService.assignUsers(id, dto.getUserIds());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/unassign")
    public ResponseEntity<?> unassignUsers(@PathVariable Long id, @RequestBody AssignRequestDTO dto) {
        taskService.unassignUsers(id, dto.getUserIds());
        return ResponseEntity.ok().build();
    }

    private TaskResponseStatusCodeDTO convertToWithStatusDto(Task task,Long userId) {
        String status = taskService.getTaskStatus(task.getId(),userId);
        return TaskResponseStatusCodeDTO.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .priority(task.getPriority().name())
                .deadline(task.getDeadline())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .tagNames(task.getTags().stream().map(Tag::getName).collect(Collectors.toSet()))
                .assigneeEmails(task.getAssignments().stream()
                        .map(mapping -> mapping.getUser().getEmail())
                        .collect(Collectors.toSet()))
                .status(status)
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
                .tagNames(task.getTags().stream().map(Tag::getName).collect(Collectors.toSet()))
                .assignees(task.getAssignments().stream().map(utm -> utm.getUser().getEmail()).collect(Collectors.toSet()))
                .build();
    }
}
