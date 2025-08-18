package com.consultadd.taskmanager.service;

import com.consultadd.taskmanager.dto.TaskReponseDTO;
import com.consultadd.taskmanager.dto.TaskRequestDTO;
import com.consultadd.taskmanager.model.Task;
import com.consultadd.taskmanager.model.User;
import com.consultadd.taskmanager.repository.TaskRepository;
import com.consultadd.taskmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public List<Task> getAllTasksById(Long id){
        return taskRepository.findAllByCreatedBy_Id(id);
    }

    public Task createTask(TaskRequestDTO taskRequestDTO, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    return new RuntimeException("User not found with email: " + email);
                });

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
                .createdBy(user)
                .build();

        try {
            Task savedTask = taskRepository.save(task);
            return (savedTask);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save task: " + e.getMessage());
        }
    }

    public Task updateTask(Long taskId, TaskRequestDTO updatedTaskData) {
        Task existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        existingTask.setTitle(updatedTaskData.getTitle());
        existingTask.setDescription(updatedTaskData.getDescription());
        existingTask.setPriority(updatedTaskData.getPriority());
        existingTask.setDeadline(updatedTaskData.getDeadline());
        existingTask.setUpdatedAt(LocalDateTime.now());

        return taskRepository.save(existingTask);
    }

    public void deleteTask(Long taskId) {
        taskRepository.deleteById(taskId);
    }

    private TaskReponseDTO mapToResponseDTO(Task task) {
        TaskReponseDTO dto = new TaskReponseDTO();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setPriority(task.getPriority());
        dto.setDeadline(task.getDeadline());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());
        return dto;
    }
}
