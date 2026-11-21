package com.indigo.model.event;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
@Builder
public class FlightRoomAssignedEvent {
    private final AssignmentEventType eventType;
    private final AssignmentUpdateReason updateReason;
    private final String flightId;
    private final List<String> addedDeskIds;
    private final List<String> removedDeskIds;
    private final List<String> assignedDeskIds;
    private final List<String> assignedUserIds;
    private final List<String> removedUserIds;
    private final Instant timestamp;
}
