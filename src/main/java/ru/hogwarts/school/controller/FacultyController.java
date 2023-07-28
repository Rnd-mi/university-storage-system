package ru.hogwarts.school.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.service.FacultyService;

@RestController
@RequestMapping("/faculty")
public class FacultyController {
    private final FacultyService facultyService;

    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @PostMapping
    public Faculty createFaculty(@RequestBody Faculty faculty) {
        return facultyService.createFaculty(faculty);
    }

    @GetMapping("{id}")
    public ResponseEntity<Faculty> getFaculty(@PathVariable long id) {
        Faculty faculty = facultyService.getFaculty(id);
        if (faculty == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(faculty);
    }

    @PutMapping
    public ResponseEntity<Faculty> updateFacultyInfo(@RequestBody Faculty faculty) {
        Faculty updated = facultyService.updateFacultyInfo(faculty);
        if (updated == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(faculty);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Faculty> deleteFaculty(@PathVariable long id) {
        Faculty deleted = facultyService.deleteFaculty(id);
        if (deleted == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(deleted);
    }
}
