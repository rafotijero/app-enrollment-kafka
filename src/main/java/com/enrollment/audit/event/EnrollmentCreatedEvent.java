package com.enrollment.audit.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentCreatedEvent {
    private String eventId;
    private String enrollmentId;
    private String studentId;
    private String studentName;
    private String studentEmail;
    private String courseId;
    private String courseName;
    private String instructor;
    private LocalDateTime timestamp;
    private String eventType = "ENROLLMENT_CREATED";

    public EnrollmentCreatedEvent(String eventId, String enrollmentId, String studentId,
                                 String studentName, String studentEmail, String courseId,
                                 String courseName, String instructor, LocalDateTime timestamp) {
        this.eventId = eventId;
        this.enrollmentId = enrollmentId;
        this.studentId = studentId;
        this.studentName = studentName;
        this.studentEmail = studentEmail;
        this.courseId = courseId;
        this.courseName = courseName;
        this.instructor = instructor;
        this.timestamp = timestamp;
        this.eventType = "ENROLLMENT_CREATED";
    }
}
