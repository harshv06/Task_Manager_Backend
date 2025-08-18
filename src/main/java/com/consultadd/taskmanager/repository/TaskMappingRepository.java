package com.consultadd.taskmanager.repository;

import com.consultadd.taskmanager.model.Task;
import com.consultadd.taskmanager.model.TaskMapping;
import com.consultadd.taskmanager.model.TaskMappingId;
import com.consultadd.taskmanager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskMappingRepository extends JpaRepository<TaskMapping, TaskMappingId> {
    List<TaskMapping> findByUser(User user);
    List<TaskMapping> findByTask(Task task);
    Optional<TaskMapping> findByUserAndTask(User user, Task task);
}
