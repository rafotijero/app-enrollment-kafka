package com.enrollment.audit.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseChangedEvent {
    private String eventId;
    private String enrollmentId;
    private String studentId;
    private String oldCourseId;
    private String newCourseId;
    private String reason;
    private LocalDateTime timestamp;
    private String eventType = "COURSE_CHANGED";

    public CourseChangedEvent(String eventId, String enrollmentId, String studentId,
                             String oldCourseId, String newCourseId, String reason,
                             LocalDateTime timestamp) {
        this.eventId = eventId;
        this.enrollmentId = enrollmentId;
        this.studentId = studentId;
        this.oldCourseId = oldCourseId;
        this.newCourseId = newCourseId;
        this.reason = reason;
        this.timestamp = timestamp;
        this.eventType = "COURSE_CHANGED";
    }
}
