package com.indigo.mapper;

import com.indigo.model.FlightAssignment;
import com.indigo.model.context.DeskAssignmentDiff;
import com.indigo.model.context.RoomActivationContext;
import com.indigo.model.event.AssignmentEventType;
import com.indigo.model.event.AssignmentUpdateReason;
import com.indigo.model.event.FlightRoomAssignedEvent;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Component
public final class AssignmentEventMapper {
    public FlightRoomAssignedEvent map(RoomActivationContext context) {
        Objects.requireNonNull(context, "Room activation context is required");
        FlightAssignment assignment = Objects.requireNonNull(context.getSavedAssignment(), "Saved assignment is required");
        DeskAssignmentDiff diff = Objects.requireNonNull(context.getDeskAssignmentDiff(), "Desk assignment diff is required");
        return FlightRoomAssignedEvent.builder()
                .eventType(AssignmentEventType.ASSIGNMENT_CHANGED)
                .updateReason(context.isWasReactivated()
                        ? AssignmentUpdateReason.ROOM_REACTIVATED : AssignmentUpdateReason.ACTIVE_ROOM)
                .flightId(assignment.getFlightId())
                .addedDeskIds(diff.getAddedDeskIds())
                .removedDeskIds(diff.getRemovedDeskIds())
                .assignedDeskIds(assignment.getAssignedDeskIds())
                .assignedUserIds(assignment.getAssignedUserIds())
                .removedUserIds(List.of())
                .timestamp(Instant.now())
                .build();
    }
}
