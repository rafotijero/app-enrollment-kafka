package com.enrollment.audit.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentCancelledEvent {
    private String eventId;
    private String enrollmentId;
    private String studentId;
    private String courseId;
    private String reason;
    private LocalDateTime timestamp;
    private String eventType = "ENROLLMENT_CANCELLED";

    public EnrollmentCancelledEvent(String eventId, String enrollmentId, String studentId,
                                   String courseId, String reason, LocalDateTime timestamp) {
        this.eventId = eventId;
        this.enrollmentId = enrollmentId;
        this.studentId = studentId;
        this.courseId = courseId;
        this.reason = reason;
        this.timestamp = timestamp;
        this.eventType = "ENROLLMENT_CANCELLED";
    }
}
