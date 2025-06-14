package com.cuu.backend.disciplinas_service.Controllers.Response;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class ApiResponse<T> {
    private HttpStatus status;
    private String message;
    private T data;
    private boolean success;
}
