package com.indigo.model.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OutgoingEventEnvelope {

    private EventMetadata metadata;
    private Object payload;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class EventMetadata {
        private String eventId;
        private String eventType;
        private String eventVersion;
        private String topic;
        private String producerService;
        private String messageKey;
        private String eventReceivedTime;
        private String eventPublishedTime;
        private String correlationId;
        private String contentType;
        private String environment;
    }
}
