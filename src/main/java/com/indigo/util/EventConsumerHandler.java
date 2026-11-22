package com.indigo.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.indigo.model.event.EventEnvelope;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.function.Consumer;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventConsumerHandler {

    private final ObjectMapper objectMapper;

    public <E> void handle(EventEnvelope<?> envelope, Acknowledgment acknowledgment,
                           Class<E> eventType, Consumer<E> processor) {
        if (Objects.isNull(envelope) || Objects.isNull(envelope.getPayload())) {
            log.warn("Received null or empty {} event payload", eventType.getSimpleName());
            acknowledge(acknowledgment);
            return;
        }

        E event = objectMapper.convertValue(envelope.getPayload(), eventType);
        if (Objects.isNull(event)) {
            log.warn("Failed to parse {} from payload", eventType.getSimpleName());
            acknowledge(acknowledgment);
            return;
        }
        processor.accept(event);
        acknowledge(acknowledgment);
    }

    private void acknowledge(Acknowledgment acknowledgment) {
        if (Objects.nonNull(acknowledgment)) {
            acknowledgment.acknowledge();
        }
    }
}
