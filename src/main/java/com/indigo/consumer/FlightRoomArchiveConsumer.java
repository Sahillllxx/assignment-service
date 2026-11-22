package com.indigo.consumer;

import com.indigo.model.context.RoomArchiveContext;
import com.indigo.model.event.EventEnvelope;
import com.indigo.model.event.FlightRoomArchivedEvent;
import com.indigo.util.EventConsumerHandler;
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
public class FlightRoomArchiveConsumer {

    private final EventConsumerHandler eventConsumerHandler;
    private final EventWorkFlowProcessor<FlightRoomArchivedEvent, RoomArchiveContext> eventWorkFlowProcessor;

    @KafkaListener(topics = "${converse.kafka.topics.room-archive}")
    public void handleRoomArchive(@Payload EventEnvelope<?> eventEnvelope, Acknowledgment acknowledgment) {
        eventConsumerHandler.handle(eventEnvelope, acknowledgment, FlightRoomArchivedEvent.class,eventWorkFlowProcessor::process);
    }

}
