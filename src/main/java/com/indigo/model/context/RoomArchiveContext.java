package com.indigo.model.context;

import com.indigo.model.FlightAssignment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
public class RoomArchiveContext {
    private String id;
    private String status;
    private Instant archivedAt;
    private FlightAssignment existingAssignment;
    private List<String> previousDeskIds;
    private List<String> previousUserIds;
    private FlightAssignment savedAssignment;
    private Boolean cacheSynced;
    private Boolean eventPublishSubmitted;
}
