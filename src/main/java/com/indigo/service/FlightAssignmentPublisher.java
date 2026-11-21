package com.indigo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.indigo.design.cor.responsibilities.events.producer.EventPublisher;
import com.indigo.model.event.FlightRoomAssignedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
public final class FlightAssignmentPublisher implements EventPublisher<FlightRoomAssignedEvent, Boolean> {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String topic;

    public FlightAssignmentPublisher(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper,
                                     @Value("${converse.kafka.topics.assignment-created}") String topic) {

        this.kafkaTemplate = Objects.requireNonNull(kafkaTemplate, "Kafka template is required");
        this.objectMapper = Objects.requireNonNull(objectMapper, "Object mapper is required").copy()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        if (topic == null || topic.isBlank()) {
            throw new IllegalArgumentException("Kafka topic is required");
        }
        this.topic = topic;
    }

    @Override
    public Boolean publish(FlightRoomAssignedEvent event) {
        Objects.requireNonNull(event, "Assignment event is required");
        if (event.getFlightId() == null || event.getFlightId().isBlank()) {
            throw new IllegalArgumentException("Assignment event flight ID must not be blank");
        }

        String payload;
        try {
            payload = objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException exception) {
            log.error("Failed to serialize assignment event for flightId={}", event.getFlightId(), exception);
            return false;
        }
        try {
            kafkaTemplate.send(topic, event.getFlightId(), payload).whenComplete((result, exception) -> {
                if (exception != null) {
                    log.error("Failed to publish assignment event for flightId={}", event.getFlightId(), exception);
                    return;
                }
                log.info("Published assignment event for flightId={}, updateReason={}",
                        event.getFlightId(), event.getUpdateReason());
            });
            return true;
        } catch (KafkaException | org.apache.kafka.common.KafkaException exception) {
            log.error("Failed to submit assignment event for flightId={}", event.getFlightId(), exception);
            return false;
        }
    }
}
