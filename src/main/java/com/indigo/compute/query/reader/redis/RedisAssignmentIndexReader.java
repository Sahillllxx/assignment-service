package com.indigo.compute.query.reader.redis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
        AssignmentIndexSnapshot snapshot = AssignmentIndexSnapshot.empty();
        if (room == null) return snapshot;

        boolean stationRequested = hasStation(room);
        boolean registrationRequested = StringUtils.hasText(room.getAircraftRegistration());
        boolean typeRequested = StringUtils.hasText(room.getAircraftType());

        List<String> requestedIndexKeys = requestedIndexKeys(room);
        if (requestedIndexKeys.isEmpty()) return snapshot;

        List<Object> indexValues = readIndexValues(requestedIndexKeys);
        if (indexValues == null) return snapshot;

        populateSnapshot(snapshot, indexValues, stationRequested, registrationRequested, typeRequested);

        return snapshot;
    }

    private Map<String, List<DeskAssignmentRule>> ruleIndex(Object value) {
        return value == null ? Map.of() : objectMapper.convertValue(value, RULE_INDEX);
    }

    private Map<String, List<String>> aircraftZoneIndex(Object value) {
        return value == null ? Map.of() : objectMapper.convertValue(value, AIRCRAFT_ZONE_INDEX);
    }

    private List<Object> readIndexValues(List<String> requestedIndexKeys) {
        List<Object> indexValues;
        try {
            indexValues = redisTemplate.opsForValue().multiGet(requestedIndexKeys);
        } catch (RuntimeException ex) {
            log.error("Failed to read assignment indexes for Redis keys={}", requestedIndexKeys, ex);
            return null;
        }

        if (indexValues == null) {
            log.error("Redis multiGet returned no response for Redis keys={}", requestedIndexKeys);
            return null;
        }

        if (indexValues.size() != requestedIndexKeys.size()) {
            log.error("Redis multiGet returned {} values for {} requested Redis keys={}",
                    indexValues.size(), requestedIndexKeys.size(), requestedIndexKeys);
            return null;
        }

        return indexValues;
    }

    private List<String> requestedIndexKeys(FlightRoomDetails room) {
        List<String> indexKeys = new ArrayList<>();

        if (hasStation(room)) {
            indexKeys.add(redisIndexProperties.getStationKey());
        }
        if (StringUtils.hasText(room.getAircraftRegistration())) {
            indexKeys.add(redisIndexProperties.getAircraftRegistrationKey());
            indexKeys.add(redisIndexProperties.getAircraftZoneKey());
            indexKeys.add(redisIndexProperties.getZoneKey());
        }
        if (StringUtils.hasText(room.getAircraftType())) {
            indexKeys.add(redisIndexProperties.getAircraftTypeKey());
        }

        return indexKeys;
    }

    private boolean hasStation(FlightRoomDetails room) {
        return StringUtils.hasText(room.getStartStation())
                || StringUtils.hasText(room.getEndStation());
    }

    private void populateSnapshot(
            AssignmentIndexSnapshot snapshot,
            List<Object> indexValues,
            boolean stationRequested,
            boolean registrationRequested,
            boolean typeRequested) {
        int valueIndex = 0;

        if (stationRequested) {
            snapshot.setStationIndexes(ruleIndex(indexValues.get(valueIndex++)));
        }
        if (registrationRequested) {
            snapshot.setAircraftRegistrationIndexes(ruleIndex(indexValues.get(valueIndex++)));
            snapshot.setAircraftZoneIndexes(aircraftZoneIndex(indexValues.get(valueIndex++)));
            snapshot.setZoneIndexes(ruleIndex(indexValues.get(valueIndex++)));
        }
        if (typeRequested) {
            snapshot.setAircraftTypeIndexes(ruleIndex(indexValues.get(valueIndex)));
        }
    }
}