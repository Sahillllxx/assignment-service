package com.indigo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.indigo.config.AssignmentRedisProperties;
import com.indigo.design.cor.responsibilities.cache.CacheClient;
import com.indigo.model.context.DeskAssignmentDiff;
import com.indigo.model.FlightAssignment;
import com.indigo.model.context.RoomActivationContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public final class AssignmentCacheService implements CacheClient<RoomActivationContext, Boolean> {

    private static final TypeReference<List<String>> FLIGHT_IDS = new TypeReference<>() {};
    private final StringRedisTemplate redis;
    private final ObjectMapper objectMapper;
    private final AssignmentRedisProperties properties;

    @Override
    public Boolean put(String flightId, RoomActivationContext context) {
        FlightAssignment assignment = Objects.requireNonNull(context.getSavedAssignment(), "Saved assignment is required");
        DeskAssignmentDiff diff = Objects.requireNonNull(context.getDeskAssignmentDiff(), "Desk diff is required");
        if (!flightId.equals(assignment.getFlightId())) {
            throw new IllegalArgumentException("Cache key must match saved assignment flight ID");
        }
        validateKeys();
        Set<String> desks = validIds(assignment.getAssignedDeskIds(), false);
        Set<String> addedDesks = validIds(diff.getAddedDeskIds(), false);
        Set<String> removedDesks = validIds(diff.getRemovedDeskIds(), false);
        Set<String> users = assignment.getAssignedUserIds() == null
                ? null : validIds(assignment.getAssignedUserIds(), true);

        try {
            writeFlightMapping(properties.getFlightDeskMap(), flightId, desks);
            updateReverseMappings(properties.getDeskFlightMap(), flightId, removedDesks, false);
            updateReverseMappings(properties.getDeskFlightMap(), flightId, addedDesks, true);
            if (users != null) {
                writeFlightMapping(properties.getFlightUserMap(), flightId, users);
                updateReverseMappings(properties.getUserFlightMap(), flightId, users, true);
            }
            return true;
        } catch (DataAccessException exception) {
            log.error("Failed to sync Redis assignments for flightId={}", flightId, exception);
            return false;
        }
    }

    private void writeFlightMapping(String key, String flightId, Set<String> ids) {
        if (ids.isEmpty()) {
            redis.opsForHash().delete(key, flightId);
            return;
        }
        try {
            redis.opsForHash().put(key, flightId, objectMapper.writeValueAsString(ids));
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Failed to serialize Redis assignment IDs", exception);
        }
    }

    private void updateReverseMappings(String key, String flightId, Set<String> ids, boolean add) {
        for (String id : ids) {
            Object value = redis.opsForHash().get(key, id);
            if (value == null && !add) {
                continue;
            }
            Set<String> flights = readFlightIds(value);
            if (add) {
                flights.add(flightId);
            } else {
                flights.remove(flightId);
            }
            writeFlightMapping(key, id, flights);
        }
    }

    private Set<String> readFlightIds(Object value) {
        if (value == null) {
            return new LinkedHashSet<>();
        }
        try {
            List<String> flights = objectMapper.readValue(value.toString(), FLIGHT_IDS);
            if (flights == null || flights.stream().anyMatch(flight -> flight == null || flight.isBlank())) {
                throw new IllegalStateException("Redis flight membership must contain valid flight IDs");
            }
            return new LinkedHashSet<>(flights);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Failed to parse Redis flight membership", exception);
        }
    }

    private Set<String> validIds(List<String> ids, boolean normalize) {
        Objects.requireNonNull(ids, "Assignment IDs are required");
        Set<String> result = new LinkedHashSet<>();
        for (String id : ids) {
            if (id == null || id.isBlank()) {
                throw new IllegalArgumentException("Assignment IDs must not contain null or blank values");
            }
            result.add(normalize ? id.trim().toLowerCase(Locale.ROOT) : id);
        }
        return result;
    }

    private void validateKeys() {
        requireKey(properties.getFlightDeskMap(), "flight-desk-map");
        requireKey(properties.getDeskFlightMap(), "desk-flight-map");
        requireKey(properties.getFlightUserMap(), "flight-user-map");
        requireKey(properties.getUserFlightMap(), "user-flight-map");
    }

    private void requireKey(String key, String name) {
        if (key == null || key.isBlank()) {
            throw new IllegalStateException("Redis " + name + " must not be blank");
        }
    }
}
