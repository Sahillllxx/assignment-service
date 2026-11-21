package com.indigo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "converse.redis")
public class AssignmentRedisProperties {
    private int batchChunkSize;
    private String flightDeskMap;
    private String deskFlightMap;
    private String flightUserMap;
    private String userFlightMap;
}
