package ru.hogwarts.school.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity(name = "students")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String name;

    private int age;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "faculty_id")
    private Faculty faculty;

    public Student(long id, String name, int age, Faculty faculty) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.faculty = faculty;
    }

    public Student() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Faculty getFaculty() {
        return faculty;
    }

    public void setFaculty(Faculty faculty) {
        this.faculty = faculty;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return id == student.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Student: " + "id = " + id + ", name = " + name + ", age = " + age;
    }
}
