package com.consultadd.taskmanager.repository;

import com.consultadd.taskmanager.model.Comment;
import com.consultadd.taskmanager.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Long> {
    List<Comment> findByTask(Task task);
}
