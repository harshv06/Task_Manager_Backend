package com.consultadd.taskmanager.config;

import com.consultadd.taskmanager.model.Status;
import com.consultadd.taskmanager.repository.StatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class DataLoader implements CommandLineRunner {

    private final StatusRepository statusRepository;

    @Override
    public void run(String... args) throws Exception {
        // Initialize default statuses if they don't exist
        if (statusRepository.count() == 0) {
            statusRepository.save(Status.builder().name(Status.StatusName.TODO).build());
            statusRepository.save(Status.builder().name(Status.StatusName.IN_PROGRESS).build());
            statusRepository.save(Status.builder().name(Status.StatusName.DONE).build());
        }
    }
}
