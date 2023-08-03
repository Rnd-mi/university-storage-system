package ru.hogwarts.school.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST,
                reason = "Age should be between 7 and 20")
public class BadAgeException extends RuntimeException {
}
