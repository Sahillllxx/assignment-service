package com.indigo.mapper;

import com.indigo.model.FlightAssignment;
import com.indigo.model.context.RoomArchiveContext;
import com.indigo.model.event.AssignmentEventType;
import com.indigo.model.event.AssignmentUpdateReason;
import com.indigo.model.event.FlightRoomAssignedEvent;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Component
public final class ArchiveEventMapper {

    public FlightRoomAssignedEvent map(RoomArchiveContext context) {
        Objects.requireNonNull(context, "Room archive context is required");
        FlightAssignment assignment = Objects.requireNonNull(context.getSavedAssignment(), "Saved assignment is required");
        return FlightRoomAssignedEvent.builder()
                .eventType(AssignmentEventType.ASSIGNMENT_CHANGED)
                .updateReason(AssignmentUpdateReason.ROOM_ARCHIVED)
                .flightId(assignment.getFlightId())
                .addedDeskIds(List.of())
                .removedDeskIds(Objects.requireNonNull(context.getPreviousDeskIds(), "Previous desk IDs are required"))
                .assignedDeskIds(List.of())
                .assignedUserIds(List.of())
                .removedUserIds(Objects.requireNonNull(context.getPreviousUserIds(), "Previous user IDs are required"))
                .timestamp(Instant.now())
                .build();
    }
}
