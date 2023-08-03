package ru.hogwarts.school.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.service.FacultyServiceImpl;

import java.util.Collection;

@RestController
@RequestMapping("/faculties")
public class FacultyController {
    private final FacultyServiceImpl facultyService;

    public FacultyController(FacultyServiceImpl facultyService) {
        this.facultyService = facultyService;
    }

    @PostMapping
    public Faculty createFaculty(@RequestBody Faculty faculty) {
        return facultyService.createFaculty(faculty);
    }

    @GetMapping("{id}")
    public Faculty getFaculty(@PathVariable long id) {
        return facultyService.getFaculty(id);
    }

    @PutMapping
    public Faculty updateFacultyInfo(@RequestBody Faculty faculty) {
        return facultyService.updateFaculty(faculty);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Faculty> deleteFaculty(@PathVariable long id) {
        facultyService.deleteFaculty(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public Collection<Faculty> getFacultiesOfColor(@RequestParam String color) {
        return facultyService.getFacultiesOfColor(color);
    }

    @GetMapping("/multi-search")
    public Collection<Faculty> getFacultyByColorOrName(@RequestParam (required = false) String color,
                                           @RequestParam (required = false) String name) {
        return facultyService.getFacultyByColorOrName(color, name);
    }

    @GetMapping
    public Collection<Faculty> getAll() {
        return facultyService.getAll();
    }
}
