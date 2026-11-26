package com.indigo.compute.query.reader.redis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.indigo.compute.index.model.AssignmentIndexSnapshot;
import com.indigo.compute.index.model.DeskAssignmentRule;
import com.indigo.compute.query.reader.AssignmentIndexReader;
import com.indigo.config.DeskAssignmentRedisConfig;
import com.indigo.model.event.FlightRoomDetails;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RedisAssignmentIndexReader implements AssignmentIndexReader {
    private static final TypeReference<Map<String, List<DeskAssignmentRule>>> RULE_INDEX =
            new TypeReference<>() { };
    private static final TypeReference<Map<String, List<String>>> AIRCRAFT_ZONE_INDEX =
            new TypeReference<>() { };

    private final RedisTemplate<String, Object> redisTemplate;
    private final DeskAssignmentRedisConfig redisIndexProperties;
    private final ObjectMapper objectMapper;

    @Override
    public AssignmentIndexSnapshot readIndexes(FlightRoomDetails room) {
        AssignmentIndexSnapshot snapshot = new AssignmentIndexSnapshot();
        snapshot.setStationIndexes(Map.of());
        snapshot.setAircraftRegistrationIndexes(Map.of());
        snapshot.setAircraftTypeIndexes(Map.of());
        snapshot.setZoneIndexes(Map.of());
        snapshot.setAircraftZoneIndexes(Map.of());
        if (room == null) {
            return snapshot;
        }
        boolean station = StringUtils.hasText(room.getStartStation()) || StringUtils.hasText(room.getEndStation());
        boolean registration = StringUtils.hasText(room.getAircraftRegistration());
        boolean type = StringUtils.hasText(room.getAircraftType());
        List<String> keys = new ArrayList<>();
        if (station) {
            keys.add(redisIndexProperties.getStationKey());
        }
        if (registration) {
            keys.add(redisIndexProperties.getAircraftRegistrationKey());
            keys.add(redisIndexProperties.getAircraftZoneKey());
            keys.add(redisIndexProperties.getZoneKey());
        }
        if (type) {
            keys.add(redisIndexProperties.getAircraftTypeKey());
        }
        if (keys.isEmpty()) {
            return snapshot;
        }
        List<Object> values = Objects.requireNonNull(redisTemplate.opsForValue().multiGet(keys),
                "Redis multiGet returned no response");
        if (values.size() != keys.size()) {
            throw new IllegalStateException("Redis multiGet response does not match requested indexes");
        }
        int position = 0;
        if (station) {
            snapshot.setStationIndexes(ruleIndex(values.get(position++)));
        }
        if (registration) {
            snapshot.setAircraftRegistrationIndexes(ruleIndex(values.get(position++)));
            Object aircraftZones = values.get(position++);
            snapshot.setAircraftZoneIndexes(aircraftZones == null ? Map.of()
                    : objectMapper.convertValue(aircraftZones, AIRCRAFT_ZONE_INDEX));
            snapshot.setZoneIndexes(ruleIndex(values.get(position++)));
        }
        if (type) {
            snapshot.setAircraftTypeIndexes(ruleIndex(values.get(position)));
        }
        return snapshot;
    }

    private Map<String, List<DeskAssignmentRule>> ruleIndex(Object value) {
        return value == null ? Map.of() : objectMapper.convertValue(value, RULE_INDEX);
    }
}