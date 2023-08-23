package ru.hogwarts.school.service;

import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;

import java.io.IOException;
import java.util.Collection;

public interface AvatarService {
    void uploadStudentAvatar(long studentId, MultipartFile avatar) throws IOException;

    Avatar findAvatar(long studentId);

    Avatar findAvatarOrThrow(long studentId);

    Collection<Avatar> getAvatarsPerPage(int page, int limit);
}
