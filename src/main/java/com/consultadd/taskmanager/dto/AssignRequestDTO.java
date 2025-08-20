package com.consultadd.taskmanager.dto;

import lombok.*;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignRequestDTO {
    private Set<Long> userIds;
}
