package com.consultadd.taskmanager.repository;

import com.consultadd.taskmanager.model.Task;
import com.consultadd.taskmanager.model.TaskMapping;
import com.consultadd.taskmanager.model.TaskMappingId;
import com.consultadd.taskmanager.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TaskMappingRepository extends JpaRepository<TaskMapping, TaskMappingId> {
    List<TaskMapping> findByUser(User user);
    List<TaskMapping> findByTask(Task task);
    Optional<TaskMapping> findByUserAndTask(User user, Task task);

    @Modifying
    @Transactional
    @Query("DELETE FROM TaskMapping tm WHERE tm.task = :task")
    void deleteByTask(@Param("task") Task task);
}
