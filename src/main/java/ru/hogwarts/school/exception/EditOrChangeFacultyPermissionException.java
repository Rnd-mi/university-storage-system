package ru.hogwarts.school.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST,
                reason = "Try '/faculties' resource to edit faculty. " +
                        "If you want to change student's faculty - " +
                        "'/associations'")
public class EditOrChangeFacultyPermissionException extends RuntimeException {
}
