package dev.szymon.gbenergymixbackend.exception.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ApiErrorResponse {

    private String message;
    private LocalDateTime timestamp;
}
