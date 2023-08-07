package ru.hogwarts.school.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.AssociationService;

import java.util.Collection;
import java.util.List;

@RestController()
@RequestMapping("/associations")
public class AssociationController {
    private final AssociationService associationService;

    public AssociationController(AssociationService associationService) {
        this.associationService = associationService;
    }

    @PutMapping("/student/{studentId}/change-faculty/{facultyId}")
    public Student changeFacultyForStudent(@PathVariable long studentId,
                                           @PathVariable long facultyId) {
        return associationService.changeFacultyForStudent(studentId, facultyId);
    }

    @PutMapping("/faculty/{facultyId}/change-students/{idList}")
    public Collection<Student> changeStudentsInFaculty(@PathVariable long facultyId,
                                                       @PathVariable List<Long> idList) {
        return associationService.changeStudentsInFaculty(facultyId, idList);
    }
}
