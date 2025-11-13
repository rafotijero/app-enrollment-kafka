package com.enrollment.audit.service;

import com.enrollment.audit.model.Course;
import com.enrollment.audit.model.Student;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.*;

@Service
public class MockDataService {

    private final Map<String, Student> students = new HashMap<>();
    private final Map<String, Course> courses = new HashMap<>();

    @PostConstruct
    public void init() {
        initializeStudents();
        initializeCourses();
    }

    private void initializeStudents() {
        students.put("STU001", new Student("STU001", "Juan Pérez", "juan.perez@email.com"));
        students.put("STU002", new Student("STU002", "María García", "maria.garcia@email.com"));
        students.put("STU003", new Student("STU003", "Carlos López", "carlos.lopez@email.com"));
        students.put("STU004", new Student("STU004", "Ana Martínez", "ana.martinez@email.com"));
        students.put("STU005", new Student("STU005", "Pedro Rodríguez", "pedro.rodriguez@email.com"));
        students.put("STU006", new Student("STU006", "Laura Fernández", "laura.fernandez@email.com"));
        students.put("STU007", new Student("STU007", "Diego Sánchez", "diego.sanchez@email.com"));
        students.put("STU008", new Student("STU008", "Sofia Ramírez", "sofia.ramirez@email.com"));
        students.put("STU009", new Student("STU009", "Miguel Torres", "miguel.torres@email.com"));
        students.put("STU010", new Student("STU010", "Valentina Flores", "valentina.flores@email.com"));
    }

    private void initializeCourses() {
        courses.put("CRS001", new Course("CRS001", "Introducción a Java", "Dr. Alberto Ruiz"));
        courses.put("CRS002", new Course("CRS002", "Spring Boot Avanzado", "Dra. Carmen Vega"));
        courses.put("CRS003", new Course("CRS003", "Microservicios con Kafka", "Ing. Roberto Díaz"));
        courses.put("CRS004", new Course("CRS004", "Arquitectura de Software", "Dr. Luis Morales"));
        courses.put("CRS005", new Course("CRS005", "Bases de Datos NoSQL", "Dra. Patricia Núñez"));
        courses.put("CRS006", new Course("CRS006", "DevOps y CI/CD", "Ing. Fernando Castro"));
        courses.put("CRS007", new Course("CRS007", "Desarrollo Frontend con React", "Dra. Isabel Herrera"));
        courses.put("CRS008", new Course("CRS008", "Seguridad en Aplicaciones", "Dr. Javier Mendoza"));
        courses.put("CRS009", new Course("CRS009", "Cloud Computing con AWS", "Ing. Claudia Romero"));
        courses.put("CRS010", new Course("CRS010", "Machine Learning Básico", "Dr. Ricardo Vargas"));
    }

    public Student getStudentById(String id) {
        return students.get(id);
    }

    public Course getCourseById(String id) {
        return courses.get(id);
    }

    public List<Student> getAllStudents() {
        return new ArrayList<>(students.values());
    }

    public List<Course> getAllCourses() {
        return new ArrayList<>(courses.values());
    }

    public boolean studentExists(String id) {
        return students.containsKey(id);
    }

    public boolean courseExists(String id) {
        return courses.containsKey(id);
    }

    public Student getRandomStudent() {
        List<Student> studentList = new ArrayList<>(students.values());
        Random random = new Random();
        return studentList.get(random.nextInt(studentList.size()));
    }

    public Course getRandomCourse() {
        List<Course> courseList = new ArrayList<>(courses.values());
        Random random = new Random();
        return courseList.get(random.nextInt(courseList.size()));
    }
}
