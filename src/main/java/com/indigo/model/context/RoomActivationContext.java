package com.indigo.model.context;


import com.indigo.model.FlightAssignment;
import com.indigo.model.event.AssignmentEntry;
import com.indigo.model.event.FlightRoomDetails;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
public class RoomActivationContext {
    private String id;
    private String status;
    private Instant activatedAt;
    private FlightRoomDetails roomDetail;
    private String reactivationAssignmentId;
    private FlightAssignment existingAssignment;
    private FlightAssignment savedAssignment;
    private Boolean cacheSynced;
    private Boolean eventPublishSubmitted;
    private boolean wasReactivated;
    private DeskAssignmentDiff deskAssignmentDiff;
    private List<String> assignedDeskIds;
    private List<String> assignedUserIds;
    private List<AssignmentEntry> assignments;

    public String requireFlightId() {
        String flightId = roomDetail.getId();
        if (flightId == null || flightId.isBlank()) {
            throw new IllegalArgumentException("Room details must contain a flight ID");
        }
        return flightId;
    }
}
