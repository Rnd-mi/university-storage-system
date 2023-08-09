package ru.hogwarts.school.service;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;

import java.io.IOException;

public interface AvatarService {
    void uploadStudentAvatar(long studentId, MultipartFile avatar) throws IOException;

    Avatar findAvatar(long studentId);

    Avatar findAvatarOrThrow(long studentId);
}
