package ru.yandex.practicum.filmorate.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.http.HttpStatus;

@Builder
@AllArgsConstructor
public class BadRequestException extends RuntimeException {
    private String message;
    private HttpStatus status;
}