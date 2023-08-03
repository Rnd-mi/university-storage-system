package ru.hogwarts.school.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST,
                reason = "Requirements for student properties: " +
                "1. 'name' must contains characters, " +
                "2. 'age' should be in range of 7 to 20")
public class InvalidStudentPropsException extends RuntimeException {
}
