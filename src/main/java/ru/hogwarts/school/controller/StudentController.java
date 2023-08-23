package ru.hogwarts.school.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;
import ru.hogwarts.school.service.StudentServiceImpl;

import java.util.Collection;

@RestController
@RequestMapping("/students")
public class StudentController {
    private final StudentService studentService;

    public StudentController(StudentServiceImpl studentService) {
        this.studentService = studentService;
    }

    @PostMapping("/{facultyId}")
    public Student createStudent(@RequestBody Student student,
                                 @PathVariable long facultyId) {
        return studentService.createStudent(student, facultyId);
    }

    @GetMapping("/{id}")
    public Student getStudent(@PathVariable long id) {
        return studentService.getStudent(id);
    }

    @PutMapping
    public Student updateStudentInfo(@RequestBody Student student) {
        return studentService.updateStudent(student);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Student> deleteStudent(@PathVariable long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public Collection<Student> getStudentsOfAge(@RequestParam int age) {
        return studentService.getStudentsOfAge(age);
    }

    @GetMapping("/search-between")
    public Collection<Student> getStudentsOfAgeBetween(@RequestParam int from,
                                                       @RequestParam int to) {
        return studentService.getByAgeBetween(from, to);
    }

    @GetMapping("/{id}/faculty")
    public Faculty getStudentsFaculty(@PathVariable long id) {
        return studentService.getFaculty(id);
    }

    @GetMapping
    public Collection<Student> getAll() {
        return studentService.getAll();
    }

    @GetMapping("/count")
    public long getNumberOfStudents() {
        return studentService.getNumberOfStudents();
    }

    @GetMapping("average-age")
    public long getAverageAgeOfStudents() {
        return studentService.getAverageAge();
    }

    @GetMapping("last-five")
    public Collection<Student> getLastFiveStudents() {
        return studentService.getLastFiveStudents();
    }
}
