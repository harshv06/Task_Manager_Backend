package com.consultadd.taskmanager.service;

import com.consultadd.taskmanager.dto.TaskReponseDTO;
import com.consultadd.taskmanager.dto.TaskRequestDTO;
import com.consultadd.taskmanager.model.*;
import com.consultadd.taskmanager.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final StatusRepository statusRepository;
    private final TaskMappingRepository taskMappingRepository;
    private final TagRepository tagRepository;

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public List<Task> getAllTasksById(Long id){
        if (id == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        return taskRepository.findAllByCreatedBy_Id(id);
    }

    public Task createTask(TaskRequestDTO taskRequestDTO, String email,Set<String> tagNames) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    return new RuntimeException("User not found with email: " + email);
                });


        Set<Tag> tags = tagNames.stream()
                .map(name -> tagRepository.findByName(name)
                        .orElseGet(() -> tagRepository.save(Tag.builder().name(name).build()))
                ).collect(Collectors.toSet());



        if (taskRequestDTO.getTitle() == null || taskRequestDTO.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Task title cannot be empty");
        }
        if (taskRequestDTO.getPriority() == null) {
            throw new IllegalArgumentException("Task priority cannot be null");
        }

        // Create task
        Task task = Task.builder()
                .title(taskRequestDTO.getTitle().trim())
                .description(taskRequestDTO.getDescription())
                .priority(taskRequestDTO.getPriority())
                .deadline(taskRequestDTO.getDeadline())
                .tags(tags)
                .createdBy(user)
                .build();


        Status todoStatus = statusRepository.findByName(Status.StatusName.TODO)
                .orElseThrow(() -> new RuntimeException("Default TODO status not found. Please contact administrator."));

        try {
            Task savedTask = taskRepository.save(task);
            TaskMapping mapping = TaskMapping.builder()
                    .task(savedTask)
                    .user(user)
                    .status(todoStatus)
                    .build();
            taskMappingRepository.save(mapping);
            return (savedTask);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save task: " + e.getMessage());
        }
    }

    public Task updateTask(Long taskId, TaskRequestDTO updatedTaskData,Set<String> tagNames) {
        Task existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        // Add validation for title and priority
        if (updatedTaskData.getTitle() == null || updatedTaskData.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Task title cannot be empty");
        }
        if (updatedTaskData.getPriority() == null) {
            throw new IllegalArgumentException("Task priority cannot be null");
        }

        Set<Tag> tags = tagNames.stream()
                .map(name -> tagRepository.findByName(name)
                        .orElseGet(() -> tagRepository.save(Tag.builder().name(name).build()))
                ).collect(Collectors.toSet());

        existingTask.setTitle(updatedTaskData.getTitle().trim());
        existingTask.setDescription(updatedTaskData.getDescription());
        existingTask.setPriority(updatedTaskData.getPriority());
        existingTask.setDeadline(updatedTaskData.getDeadline());
        existingTask.setUpdatedAt(LocalDateTime.now());
        existingTask.setTags(tags);

        return taskRepository.save(existingTask);
    }

    public void deleteTask(Long taskId) {
        // Add validation to check if task exists
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        // First delete all task mappings
        taskMappingRepository.deleteByTask(task);

        // Then delete the task
        taskRepository.delete(task);
    }

    public void updateTaskStatus(Long taskId, Long userId, Integer statusId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Status status = statusRepository.findById(statusId)
                .orElseThrow(() -> new RuntimeException("Status not found"));

        TaskMapping mapping = taskMappingRepository.findByUserAndTask(user,task)
                .orElseThrow(() -> new RuntimeException("Task not assigned to user"));

        mapping.setStatus(status);
        taskMappingRepository.save(mapping);
    }

    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    public String getTaskStatus(Long taskId, Long userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        TaskMapping mapping = taskMappingRepository.findByUserAndTask(user, task)
                .orElse(null);

        return mapping != null && mapping.getStatus() != null ?
                mapping.getStatus().getName().name() : "TODO";
    }

}
