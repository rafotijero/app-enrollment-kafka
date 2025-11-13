package com.enrollment.audit.controller;

import com.enrollment.audit.dto.*;
import com.enrollment.audit.event.*;
import com.enrollment.audit.model.Course;
import com.enrollment.audit.model.Enrollment;
import com.enrollment.audit.model.Student;
import com.enrollment.audit.producer.AuditEventProducer;
import com.enrollment.audit.service.MockDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/enrollment")
public class EnrollmentController {

    @Autowired
    private AuditEventProducer auditEventProducer;

    @Autowired
    private MockDataService mockDataService;

    // In-memory storage para el demo (en producción sería una base de datos)
    private final Map<String, Enrollment> enrollments = new ConcurrentHashMap<>();

    @PostMapping("/enroll")
    public ResponseEntity<Map<String, Object>> enrollStudent(@RequestBody EnrollmentRequest request) {
        // Validar que el estudiante y curso existen
        Student student = mockDataService.getStudentById(request.getStudentId());
        Course course = mockDataService.getCourseById(request.getCourseId());

        if (student == null) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Student not found: " + request.getStudentId()));
        }

        if (course == null) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Course not found: " + request.getCourseId()));
        }

        // Generar IDs únicos
        String enrollmentId = "ENR-" + UUID.randomUUID().toString();
        String eventId = "EVT-" + UUID.randomUUID().toString();

        // Crear la matrícula
        Enrollment enrollment = new Enrollment(
            enrollmentId,
            student,
            course,
            LocalDateTime.now(),
            "ACTIVE"
        );

        enrollments.put(enrollmentId, enrollment);

        // Crear y enviar evento de auditoría
        EnrollmentCreatedEvent event = new EnrollmentCreatedEvent(
            eventId,
            enrollmentId,
            student.getId(),
            student.getName(),
            student.getEmail(),
            course.getId(),
            course.getName(),
            course.getInstructor(),
            LocalDateTime.now()
        );

        auditEventProducer.sendEnrollmentCreatedEvent(event);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(Map.of(
                "message", "Enrollment created successfully",
                "enrollmentId", enrollmentId,
                "eventId", eventId,
                "student", student.getName(),
                "course", course.getName()
            ));
    }

    @PostMapping("/{enrollmentId}/cancel")
    public ResponseEntity<Map<String, Object>> cancelEnrollment(
            @PathVariable String enrollmentId,
            @RequestBody CancelEnrollmentRequest request) {

        Enrollment enrollment = enrollments.get(enrollmentId);

        if (enrollment == null) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Enrollment not found: " + enrollmentId));
        }

        if ("CANCELLED".equals(enrollment.getStatus())) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Enrollment already cancelled"));
        }

        // Actualizar estado
        enrollment.setStatus("CANCELLED");

        // Crear y enviar evento de auditoría
        String eventId = "EVT-" + UUID.randomUUID().toString();
        EnrollmentCancelledEvent event = new EnrollmentCancelledEvent(
            eventId,
            enrollmentId,
            enrollment.getStudent().getId(),
            enrollment.getCourse().getId(),
            request.getReason(),
            LocalDateTime.now()
        );

        auditEventProducer.sendEnrollmentCancelledEvent(event);

        return ResponseEntity.ok(Map.of(
            "message", "Enrollment cancelled successfully",
            "enrollmentId", enrollmentId,
            "eventId", eventId,
            "reason", request.getReason()
        ));
    }

    @PostMapping("/{enrollmentId}/change-course")
    public ResponseEntity<Map<String, Object>> changeCourse(
            @PathVariable String enrollmentId,
            @RequestBody ChangeCourseRequest request) {

        Enrollment enrollment = enrollments.get(enrollmentId);

        if (enrollment == null) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Enrollment not found: " + enrollmentId));
        }

        Course newCourse = mockDataService.getCourseById(request.getNewCourseId());

        if (newCourse == null) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Course not found: " + request.getNewCourseId()));
        }

        String oldCourseId = enrollment.getCourse().getId();

        // Actualizar curso
        enrollment.setCourse(newCourse);

        // Crear y enviar evento de auditoría
        String eventId = "EVT-" + UUID.randomUUID().toString();
        CourseChangedEvent event = new CourseChangedEvent(
            eventId,
            enrollmentId,
            enrollment.getStudent().getId(),
            oldCourseId,
            newCourse.getId(),
            request.getReason(),
            LocalDateTime.now()
        );

        auditEventProducer.sendCourseChangedEvent(event);

        return ResponseEntity.ok(Map.of(
            "message", "Course changed successfully",
            "enrollmentId", enrollmentId,
            "eventId", eventId,
            "oldCourse", oldCourseId,
            "newCourse", newCourse.getId()
        ));
    }

    @PostMapping("/{enrollmentId}/payment")
    public ResponseEntity<Map<String, Object>> registerPayment(
            @PathVariable String enrollmentId,
            @RequestBody PaymentRequest request) {

        Enrollment enrollment = enrollments.get(enrollmentId);

        if (enrollment == null) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Enrollment not found: " + enrollmentId));
        }

        // Generar IDs únicos
        String eventId = "EVT-" + UUID.randomUUID().toString();
        String paymentId = "PAY-" + UUID.randomUUID().toString();

        // Crear y enviar evento de auditoría
        PaymentCompletedEvent event = new PaymentCompletedEvent(
            eventId,
            enrollmentId,
            enrollment.getStudent().getId(),
            paymentId,
            request.getAmount(),
            request.getCurrency(),
            request.getPaymentMethod(),
            LocalDateTime.now()
        );

        auditEventProducer.sendPaymentCompletedEvent(event);

        return ResponseEntity.ok(Map.of(
            "message", "Payment registered successfully",
            "enrollmentId", enrollmentId,
            "eventId", eventId,
            "paymentId", paymentId,
            "amount", request.getAmount() + " " + request.getCurrency()
        ));
    }

    @GetMapping("/courses")
    public ResponseEntity<List<Course>> getAllCourses() {
        return ResponseEntity.ok(mockDataService.getAllCourses());
    }

    @GetMapping("/students")
    public ResponseEntity<List<Student>> getAllStudents() {
        return ResponseEntity.ok(mockDataService.getAllStudents());
    }

    @GetMapping("/{enrollmentId}")
    public ResponseEntity<Object> getEnrollment(@PathVariable String enrollmentId) {
        Enrollment enrollment = enrollments.get(enrollmentId);

        if (enrollment == null) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Enrollment not found: " + enrollmentId));
        }

        return ResponseEntity.ok(enrollment);
    }

    @GetMapping
    public ResponseEntity<List<Enrollment>> getAllEnrollments() {
        return ResponseEntity.ok(new ArrayList<>(enrollments.values()));
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "timestamp", LocalDateTime.now(),
            "totalEnrollments", enrollments.size()
        ));
    }
}
