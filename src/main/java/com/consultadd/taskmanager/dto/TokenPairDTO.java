package com.consultadd.taskmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenPairDTO {
    private String accessToken;
    private String refreshToken;
}
