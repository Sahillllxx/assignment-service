package com.indigo.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.indigo.model.event.EventEnvelope;
import com.indigo.model.event.FlightRoomActivatedEvent;
import com.indigo.model.context.RoomActivationContext;
import com.indigo.workflow.EventWorkFlowProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FlightRoomEventConsumer {

    private final ObjectMapper objectMapper;
    private final EventWorkFlowProcessor<FlightRoomActivatedEvent, RoomActivationContext> eventWorkFlowProcessor;

    @KafkaListener(topics = "${converse.kafka.topics.room-active}")
    public void handleRoomActive(@Payload EventEnvelope<?> eventEnvelope, Acknowledgment acknowledgment) {
        if (eventEnvelope == null || eventEnvelope.getPayload() == null) {
            log.warn("Received null or empty active room event payload");
            acknowledge(acknowledgment);
            return;
        }
        FlightRoomActivatedEvent roomActivatedEvent = objectMapper.convertValue(eventEnvelope.getPayload(), FlightRoomActivatedEvent.class);
        if (roomActivatedEvent == null) {
            log.warn("Failed to parse FlightRoomActivatedEvent from payload");
            acknowledge(acknowledgment);
            return;
        }
        eventWorkFlowProcessor.process(roomActivatedEvent);
        acknowledge(acknowledgment);
    }

    private void acknowledge(Acknowledgment acknowledgment) {
        if (acknowledgment != null) {
            acknowledgment.acknowledge();
        }
    }
}
