package com.enrollment.audit.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Value("${app.kafka.topic.enrollment-created}")
    private String enrollmentCreatedTopic;

    @Value("${app.kafka.topic.enrollment-cancelled}")
    private String enrollmentCancelledTopic;

    @Value("${app.kafka.topic.course-changed}")
    private String courseChangedTopic;

    @Value("${app.kafka.topic.payment-completed}")
    private String paymentCompletedTopic;

    @Bean
    public NewTopic enrollmentCreatedTopic() {
        return TopicBuilder.name(enrollmentCreatedTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic enrollmentCancelledTopic() {
        return TopicBuilder.name(enrollmentCancelledTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic courseChangedTopic() {
        return TopicBuilder.name(courseChangedTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic paymentCompletedTopic() {
        return TopicBuilder.name(paymentCompletedTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
