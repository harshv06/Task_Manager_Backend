package com.consultadd.taskmanager.repository;

import com.consultadd.taskmanager.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StatusRepository extends JpaRepository<Status,Integer> {
    Optional<Status> findByName(Status.StatusName name);


}
