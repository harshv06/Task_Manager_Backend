package com.consultadd.taskmanager.controller;

import com.consultadd.taskmanager.config.Utils;
import com.consultadd.taskmanager.dto.CommentRequestDTO;
import com.consultadd.taskmanager.dto.CommentResponseDTO;
import com.consultadd.taskmanager.model.Comment;
import com.consultadd.taskmanager.model.User;
import com.consultadd.taskmanager.service.CommentService;
import com.consultadd.taskmanager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tasks/{taskId}/comments")
public class CommentController {
    private final CommentService commentService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<CommentResponseDTO>> getComments(@PathVariable Long taskId) {
        List<Comment> comments = commentService.getCommentsByTaskId(taskId);
        return ResponseEntity.ok(comments.stream()
                .map(this::toDto)
                .collect(Collectors.toList()));
    }

    @PostMapping
    public ResponseEntity<CommentResponseDTO> addComment(
            @PathVariable Long taskId,
            @RequestBody CommentRequestDTO dto) {
        String email = Utils.getCurrentUserEmail();
        User user = userService.findByEmail(email).orElseThrow();

        Long userId = user.getId(); //  authenticated user
        Comment comment = commentService.addComment(taskId, userId, dto.getContent());
        return ResponseEntity.ok(toDto(comment));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.ok().build();
    }

    private CommentResponseDTO toDto(Comment comment) {
        return CommentResponseDTO.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .author(comment.getAuthor().getEmail())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
