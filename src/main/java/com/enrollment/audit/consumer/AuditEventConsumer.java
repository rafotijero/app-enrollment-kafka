package com.enrollment.audit.consumer;

import com.enrollment.audit.event.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class AuditEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(AuditEventConsumer.class);

    @KafkaListener(
        topics = "${app.kafka.topic.enrollment-created}",
        groupId = "${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeEnrollmentCreatedEvent(
            @Payload EnrollmentCreatedEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        logger.info("======================================");
        logger.info("AUDIT LOG - ENROLLMENT CREATED");
        logger.info("======================================");
        logger.info("Event ID: {}", event.getEventId());
        logger.info("Enrollment ID: {}", event.getEnrollmentId());
        logger.info("Student: {} ({})", event.getStudentName(), event.getStudentEmail());
        logger.info("Course: {} - Instructor: {}", event.getCourseName(), event.getInstructor());
        logger.info("Timestamp: {}", event.getTimestamp());
        logger.info("Kafka Metadata - Partition: {}, Offset: {}", partition, offset);
        logger.info("======================================\n");
    }

    @KafkaListener(
        topics = "${app.kafka.topic.enrollment-cancelled}",
        groupId = "${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeEnrollmentCancelledEvent(
            @Payload EnrollmentCancelledEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        logger.info("======================================");
        logger.info("AUDIT LOG - ENROLLMENT CANCELLED");
        logger.info("======================================");
        logger.info("Event ID: {}", event.getEventId());
        logger.info("Enrollment ID: {}", event.getEnrollmentId());
        logger.info("Student ID: {}", event.getStudentId());
        logger.info("Course ID: {}", event.getCourseId());
        logger.info("Reason: {}", event.getReason());
        logger.info("Timestamp: {}", event.getTimestamp());
        logger.info("Kafka Metadata - Partition: {}, Offset: {}", partition, offset);
        logger.info("======================================\n");
    }

    @KafkaListener(
        topics = "${app.kafka.topic.course-changed}",
        groupId = "${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeCourseChangedEvent(
            @Payload CourseChangedEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        logger.info("======================================");
        logger.info("AUDIT LOG - COURSE CHANGED");
        logger.info("======================================");
        logger.info("Event ID: {}", event.getEventId());
        logger.info("Enrollment ID: {}", event.getEnrollmentId());
        logger.info("Student ID: {}", event.getStudentId());
        logger.info("Old Course ID: {}", event.getOldCourseId());
        logger.info("New Course ID: {}", event.getNewCourseId());
        logger.info("Reason: {}", event.getReason());
        logger.info("Timestamp: {}", event.getTimestamp());
        logger.info("Kafka Metadata - Partition: {}, Offset: {}", partition, offset);
        logger.info("======================================\n");
    }

    @KafkaListener(
        topics = "${app.kafka.topic.payment-completed}",
        groupId = "${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumePaymentCompletedEvent(
            @Payload PaymentCompletedEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        logger.info("======================================");
        logger.info("AUDIT LOG - PAYMENT COMPLETED");
        logger.info("======================================");
        logger.info("Event ID: {}", event.getEventId());
        logger.info("Enrollment ID: {}", event.getEnrollmentId());
        logger.info("Student ID: {}", event.getStudentId());
        logger.info("Payment ID: {}", event.getPaymentId());
        logger.info("Amount: {} {}", event.getAmount(), event.getCurrency());
        logger.info("Payment Method: {}", event.getPaymentMethod());
        logger.info("Timestamp: {}", event.getTimestamp());
        logger.info("Kafka Metadata - Partition: {}, Offset: {}", partition, offset);
        logger.info("======================================\n");
    }
}
