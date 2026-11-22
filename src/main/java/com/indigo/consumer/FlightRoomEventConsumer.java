package com.indigo.consumer;

import com.indigo.model.context.RoomActivationContext;
import com.indigo.model.event.EventEnvelope;
import com.indigo.model.event.FlightRoomActivatedEvent;
import com.indigo.util.EventConsumerHandler;
import com.indigo.workflow.EventWorkFlowProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FlightRoomEventConsumer {

    private final EventConsumerHandler eventConsumerHandler;
    private final EventWorkFlowProcessor<FlightRoomActivatedEvent, RoomActivationContext> eventWorkFlowProcessor;

    @KafkaListener(topics = "${converse.kafka.topics.room-active}")
    public void handleRoomActive(@Payload EventEnvelope<?> eventEnvelope, Acknowledgment acknowledgment) {
        eventConsumerHandler.handle(eventEnvelope, acknowledgment, FlightRoomActivatedEvent.class, eventWorkFlowProcessor::process);
    }
}
