package com.indigo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "flight_assignments")
public class FlightAssignment {
    @Id
    private String id;

    @Indexed(unique = true)
    private String flightId;

    @Builder.Default
    @Indexed
    private List<String> assignedDeskIds = new ArrayList<>();

    @Builder.Default
    @Indexed
    private List<String> assignedUserIds = new ArrayList<>();

    @Builder.Default
    private List<AssignmentEntry> assignments = new ArrayList<>();

    @Builder.Default
    @Field("isActive")
    private boolean isActive = true;

    private Instant createdAt;
    private Instant updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AssignmentEntry {
        private String assigneeId;
        private String type;
        private String source;
        private String assignedBy;
        private Instant assignedAt;
    }
}