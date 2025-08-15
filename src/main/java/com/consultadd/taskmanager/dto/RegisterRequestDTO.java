package com.consultadd.taskmanager.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequestDTO {
    @Getter
    private String email;
    @Getter
    private String password;
    @Getter
    private String fullName;
}
