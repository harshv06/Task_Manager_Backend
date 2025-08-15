package com.consultadd.taskmanager.repository;

import com.consultadd.taskmanager.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task,Long> {
    List<Task> findAllByCreatedBy_Id(Long createdById);
}
