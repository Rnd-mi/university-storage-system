package ru.hogwarts.school.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.service.AvatarService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

@RestController
@RequestMapping("/avatars")
public class AvatarController {
    private final AvatarService avatarService;

    public AvatarController(AvatarService avatarService) {
        this.avatarService = avatarService;
    }

    @PostMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadStudentAvatar(@PathVariable long id,
                                                      @RequestParam MultipartFile avatar) throws IOException {
        avatarService.uploadStudentAvatar(id, avatar);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/preview")
    public ResponseEntity<byte[]> getStudentAvatarPreview(@PathVariable long id) {
        Avatar avatar = avatarService.findAvatarOrThrow(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(avatar.getMediaType()));
        headers.setContentLength(avatar.getPreview().length);
        return ResponseEntity.ok().headers(headers).body(avatar.getPreview());
    }

    @GetMapping("/{id}/real")
    public void getStudentAvatarInRealSize(@PathVariable long id,
                                           HttpServletResponse response) throws IOException {
        Avatar avatar = avatarService.findAvatarOrThrow(id);
        try (
                InputStream is = Files.newInputStream(Path.of(avatar.getFilePath()));
                BufferedInputStream bis = new BufferedInputStream(is, 1024);
                BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream())
        ) {
            response.setContentType(avatar.getMediaType());
            response.setContentLength(avatar.getFileSize().intValue());
            response.setStatus(HttpServletResponse.SC_OK);
            bis.transferTo(bos);
        }
    }

    @GetMapping
    public Collection<Avatar> getAllAvatarsPerPage(@RequestParam int page,
                                                   @RequestParam int limit) {
        return avatarService.getAvatarsPerPage(page, limit);
    }
}
