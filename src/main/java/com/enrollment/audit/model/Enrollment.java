package com.enrollment.audit.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Enrollment {
    private String enrollmentId;
    private Student student;
    private Course course;
    private LocalDateTime enrollmentDate;
    private String status; // ACTIVE, CANCELLED, COMPLETED
}
