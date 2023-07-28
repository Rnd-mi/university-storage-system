package ru.stepanov.skypro.project.thirdcourseproject;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition
public class ThirdCourseProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(ThirdCourseProjectApplication.class, args);
    }

}
