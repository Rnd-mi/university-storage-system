package ru.hogwarts.school.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.hogwarts.school.exception.AvatarNotFoundException;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.repository.AvatarRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static ru.hogwarts.school.constants.Constants.ID;

public class AvatarServiceImplTest {
    private AvatarRepository avatarRepository;
    private StudentService studentService;
    private AvatarService out;

    @BeforeEach
    public void setup() {
        avatarRepository = mock(AvatarRepository.class);
        studentService = mock(StudentService.class);
        out = new AvatarServiceImpl(avatarRepository, studentService);
    }

    @Test
    public void findAvatar_shouldReturnNewAvatarIfStudentHasNoAvatar() {
        when(avatarRepository.findByStudentId(ID)).thenReturn(Optional.of(new Avatar()));
        assertEquals(new Avatar(), out.findAvatar(ID));
    }

    @Test
    public void findAvatarOrThrow_shouldThrowIfStudentHasNoAvatar() {
        when(avatarRepository.findByStudentId(ID)).thenReturn(Optional.empty());
        assertThrows(AvatarNotFoundException.class, () -> out.findAvatarOrThrow(ID));
    }
}
