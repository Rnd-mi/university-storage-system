package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Student;

import java.util.Collection;
import java.util.List;

@Service
public class AssociationServiceImpl implements AssociationService {
    private final StudentService studentService;

    private final FacultyService facultyService;

    private final Logger logger = LoggerFactory.getLogger(AssociationServiceImpl.class);

    public AssociationServiceImpl(StudentService studentService, FacultyService facultyService) {
        this.studentService = studentService;
        this.facultyService = facultyService;
    }

    @Override
    public Student changeFacultyForStudent(long studentId, long facultyId) {
        logWhenMethodInvoked("changeFacultyForStudent");
        Student student = studentService.getStudent(studentId);
        return studentService.createStudent(student, facultyId);
    }

    @Override
    public Collection<Student> changeStudentsInFaculty(long facultyId, List<Long> idList) {
        logWhenMethodInvoked("changeStudentsInFaculty");
        for (Long id : idList) {
            Student student = studentService.getStudent(id);
            studentService.createStudent(student, facultyId);
        }
        return facultyService.getStudents(facultyId);
    }

    private void logWhenMethodInvoked(String methodName) {
        logger.info("Method {} was invoked", methodName);
    }
}
