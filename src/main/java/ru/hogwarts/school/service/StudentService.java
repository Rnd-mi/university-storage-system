package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Student;

import java.util.*;

@Service
public class StudentService {
    private final Map<Long, Student> students = new HashMap<>();
    private long lastId = 0;

    public Student createStudent(Student student) {
        student.setId(++lastId);
        students.put(lastId, student);
        return student;
    }

    public Student getStudent(long id) {
        return students.get(id);
    }

    public Student updateStudentInfo(Student student) {
        if (students.containsKey(student.getId())) {
            return students.put(student.getId(), student);
        }
        return null;
    }

    public Student deleteStudent(long id) {
        return students.remove(id);
    }

    public List<Student> getStudentsOfAge(int age) {
        List<Student> result = new ArrayList<>();

        for (Student student : students.values()) {
            if (student.getAge() == age) {
                result.add(student);
            }
        }
        return result;
    }
}
