package ru.hogwarts.school.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST,
        reason = "Requirements for faculty properties: " +
                "'name' and 'color' must contains characters")
public class InvalidFacultyPropsException extends RuntimeException {
}
