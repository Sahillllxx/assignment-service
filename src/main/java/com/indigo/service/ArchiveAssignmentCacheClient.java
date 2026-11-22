package com.indigo.service;

import com.indigo.design.cor.responsibilities.cache.CacheClient;
import com.indigo.model.FlightAssignment;
import com.indigo.model.context.RoomArchiveContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public final class ArchiveAssignmentCacheClient implements CacheClient<RoomArchiveContext, Boolean> {

    private final AssignmentCacheService assignmentCacheService;

    @Override
    public Boolean put(String flightId, RoomArchiveContext context) {
        FlightAssignment assignment = Objects.requireNonNull(context.getSavedAssignment(), "Saved assignment is required");
        if (!flightId.equals(assignment.getFlightId())) {
            throw new IllegalArgumentException("Cache key must match saved assignment flight ID");
        }
        return assignmentCacheService.archiveFlight(flightId, context.getPreviousDeskIds(), context.getPreviousUserIds());
    }
}
