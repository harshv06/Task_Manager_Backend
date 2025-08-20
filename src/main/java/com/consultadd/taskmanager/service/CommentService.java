package com.consultadd.taskmanager.service;

import com.consultadd.taskmanager.model.Comment;
import com.consultadd.taskmanager.model.Task;
import com.consultadd.taskmanager.model.TaskMapping;
import com.consultadd.taskmanager.model.User;
import com.consultadd.taskmanager.repository.CommentRepository;
import com.consultadd.taskmanager.repository.TaskMappingRepository;
import com.consultadd.taskmanager.repository.TaskRepository;
import com.consultadd.taskmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskMappingRepository userTaskMappingRepository;

    public List<Comment> getCommentsByTaskId(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        return commentRepository.findByTask(task);
    }

    public Comment addComment(Long taskId, Long userId, String content) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));


        Comment comment = Comment.builder()
                .content(content)
                .task(task)
                .author(user)
                .createdAt(LocalDateTime.now())
                .build();

        Set<User> taskMembers = userTaskMappingRepository.findByTask(comment.getTask())
                        .stream().map(TaskMapping::getUser).collect(Collectors.toSet());

        taskMembers.remove(comment.getAuthor()); // Don’t notify self

        return commentRepository.save(comment);
    }

    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }
}
