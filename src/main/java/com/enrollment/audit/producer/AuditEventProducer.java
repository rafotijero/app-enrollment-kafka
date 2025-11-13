package com.enrollment.audit.producer;

import com.enrollment.audit.event.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class AuditEventProducer {

    private static final Logger logger = LoggerFactory.getLogger(AuditEventProducer.class);

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.topic.enrollment-created}")
    private String enrollmentCreatedTopic;

    @Value("${app.kafka.topic.enrollment-cancelled}")
    private String enrollmentCancelledTopic;

    @Value("${app.kafka.topic.course-changed}")
    private String courseChangedTopic;

    @Value("${app.kafka.topic.payment-completed}")
    private String paymentCompletedTopic;

    public void sendEnrollmentCreatedEvent(EnrollmentCreatedEvent event) {
        logger.info("Sending EnrollmentCreatedEvent: {}", event.getEventId());

        CompletableFuture<SendResult<String, Object>> future =
            kafkaTemplate.send(enrollmentCreatedTopic, event.getEnrollmentId(), event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                logger.info("Event sent successfully: {} with offset: {}",
                    event.getEventId(),
                    result.getRecordMetadata().offset());
            } else {
                logger.error("Failed to send event: {}", event.getEventId(), ex);
            }
        });
    }

    public void sendEnrollmentCancelledEvent(EnrollmentCancelledEvent event) {
        logger.info("Sending EnrollmentCancelledEvent: {}", event.getEventId());

        CompletableFuture<SendResult<String, Object>> future =
            kafkaTemplate.send(enrollmentCancelledTopic, event.getEnrollmentId(), event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                logger.info("Event sent successfully: {} with offset: {}",
                    event.getEventId(),
                    result.getRecordMetadata().offset());
            } else {
                logger.error("Failed to send event: {}", event.getEventId(), ex);
            }
        });
    }

    public void sendCourseChangedEvent(CourseChangedEvent event) {
        logger.info("Sending CourseChangedEvent: {}", event.getEventId());

        CompletableFuture<SendResult<String, Object>> future =
            kafkaTemplate.send(courseChangedTopic, event.getEnrollmentId(), event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                logger.info("Event sent successfully: {} with offset: {}",
                    event.getEventId(),
                    result.getRecordMetadata().offset());
            } else {
                logger.error("Failed to send event: {}", event.getEventId(), ex);
            }
        });
    }

    public void sendPaymentCompletedEvent(PaymentCompletedEvent event) {
        logger.info("Sending PaymentCompletedEvent: {}", event.getEventId());

        CompletableFuture<SendResult<String, Object>> future =
            kafkaTemplate.send(paymentCompletedTopic, event.getEnrollmentId(), event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                logger.info("Event sent successfully: {} with offset: {}",
                    event.getEventId(),
                    result.getRecordMetadata().offset());
            } else {
                logger.error("Failed to send event: {}", event.getEventId(), ex);
            }
        });
    }
}
