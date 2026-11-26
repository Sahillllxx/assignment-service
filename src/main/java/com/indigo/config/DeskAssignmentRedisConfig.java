package com.indigo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "desk-assignment.redis")
public class DeskAssignmentRedisConfig {

    private String keyPrefix;
    private String stationKey;
    private String aircraftRegistrationKey;
    private String aircraftTypeKey;
    private String zoneKey;
    private String aircraftZoneKey;
}