package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.exception.AvatarNotFoundException;
import ru.hogwarts.school.exception.FileIsTooBigException;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.AvatarRepository;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Service
@Transactional
public class AvatarServiceImpl implements AvatarService {

    @Value("${students.avatars.dir.path}")
    private String avatarsDir;

    private final AvatarRepository avatarRepository;

    private final StudentService studentService;

    private final Logger logger = LoggerFactory.getLogger(AvatarServiceImpl.class);

    public AvatarServiceImpl(AvatarRepository avatarRepository, StudentService studentService) {
        this.avatarRepository = avatarRepository;
        this.studentService = studentService;
    }

    @Override
    public void uploadStudentAvatar(long studentId, MultipartFile avatarFile) throws IOException {
        logWhenMethodInvoked("uploadStudentAvatar");
        long imageSize = avatarFile.getSize();
        if (imageSize > (1024 * 300)) {
            logger.error("Upload image is too big for avatar. Size = {}", imageSize);
            throw new FileIsTooBigException();
        }

        Student student = studentService.getStudent(studentId);
        logger.debug("Creating directory if absent, deleting image if already exists");
        Path filePath = Path.of(avatarsDir, studentId + "." +
                getExtension(avatarFile.getOriginalFilename()));
        Files.createDirectories(filePath.getParent());
        Files.deleteIfExists(filePath);
        try (
                InputStream is = avatarFile.getInputStream();
                OutputStream os = Files.newOutputStream(filePath, CREATE_NEW);
                BufferedInputStream bis = new BufferedInputStream(is, 1024);
                BufferedOutputStream bos = new BufferedOutputStream(os, 1024)
        ) {
            bis.transferTo(bos);
        }
        logger.debug("Filling avatar object with values and saving in repo");
        Avatar avatar = findAvatar(studentId);
        avatar.setFilePath(filePath.toString());
        avatar.setFileSize(avatarFile.getSize());
        avatar.setMediaType(avatarFile.getContentType());
        avatar.setPreview(generateImagePreview(filePath));
        avatar.setStudent(student);
        avatarRepository.save(avatar);
    }

    @Override
    public Avatar findAvatar(long studentId) {
        logWhenMethodInvoked("findAvatar");
        return avatarRepository.findByStudentId(studentId).orElse(new Avatar());
    }

    @Override
    public Avatar findAvatarOrThrow(long studentId) {
        logWhenMethodInvoked("FindAvatarOrThrow");
        Avatar avatar = avatarRepository.findByStudentId(studentId).orElse(null);

        if (avatar == null) {
            logger.error("User with id = {} has no avatar", studentId);
            throw new AvatarNotFoundException();
        }
        return avatar;
    }

    @Override
    public Collection<Avatar> getAvatarsPerPage(int page, int limit) {
        logWhenMethodInvoked("getAvatarsPerPage");
        return avatarRepository.findAll(PageRequest.of(page - 1, limit)).getContent();
    }

    private byte[] generateImagePreview(Path filePath) throws IOException {
        logWhenMethodInvoked("generateImagePreview");
        try (
                InputStream is = Files.newInputStream(filePath);
                BufferedInputStream bis = new BufferedInputStream(is, 1024);
                ByteArrayOutputStream baos = new ByteArrayOutputStream()
            ) {
            BufferedImage image = ImageIO.read(bis);

            logger.debug("Creating avatar preview and return result of it as byte array");
            int height = image.getHeight() / (image.getWidth() / 100);
            BufferedImage preview = new BufferedImage(100, height, image.getType());
            Graphics2D graphics = preview.createGraphics();
            graphics.drawImage(image, 0, 0, 100, height, null);
            graphics.dispose();
            ImageIO.write(preview, getExtension(filePath.getFileName().toString()), baos);
            return baos.toByteArray();
        }
    }

    private String getExtension(String filename) {
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

    private void logWhenMethodInvoked(String methodName) {
        logger.info("Method '{}' has been invoked", methodName);
    }
}
