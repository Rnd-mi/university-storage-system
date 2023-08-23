package ru.hogwarts.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.hogwarts.school.model.Student;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findByAge(int age);

    List<Student> findByAgeBetween(int from, int to);

    List<Student> findStudentsByFacultyId(long facultyId);

    @Query(value = "SELECT COUNT(*) FROM students", nativeQuery = true)
    long countAllStudents();

    @Query(value = "SELECT AVG(age) FROM students", nativeQuery = true)
    long getAverageAge();

    @Query(value = "SELECT * FROM students ORDER BY id DESC LIMIT 5", nativeQuery = true)
    List<Student> findLastFiveStudents();
}
