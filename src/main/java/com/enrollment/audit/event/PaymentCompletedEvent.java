package com.enrollment.audit.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCompletedEvent {
    private String eventId;
    private String enrollmentId;
    private String studentId;
    private String paymentId;
    private Double amount;
    private String currency;
    private String paymentMethod;
    private LocalDateTime timestamp;
    private String eventType = "PAYMENT_COMPLETED";

    public PaymentCompletedEvent(String eventId, String enrollmentId, String studentId,
                                String paymentId, Double amount, String currency,
                                String paymentMethod, LocalDateTime timestamp) {
        this.eventId = eventId;
        this.enrollmentId = enrollmentId;
        this.studentId = studentId;
        this.paymentId = paymentId;
        this.amount = amount;
        this.currency = currency;
        this.paymentMethod = paymentMethod;
        this.timestamp = timestamp;
        this.eventType = "PAYMENT_COMPLETED";
    }
}
