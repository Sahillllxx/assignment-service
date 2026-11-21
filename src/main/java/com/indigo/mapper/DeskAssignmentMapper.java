package com.indigo.mapper;

import com.indigo.model.context.RoomActivationContext;
import com.indigo.model.event.AssignmentEntry;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public final class DeskAssignmentMapper {

    public static final String SYSTEM = "SYSTEM";

    public void appendAssignments(RoomActivationContext context, List<String> deskIds) {
        Objects.requireNonNull(context, "Room activation context is required");
        validateDeskIds(deskIds);
        Objects.requireNonNull(context.getAssignments(), "Context assignments must be initialized");
        List<AssignmentEntry> deskAssignments = createAssignments(deskIds);
        context.setAssignedDeskIds(new ArrayList<>(deskIds));
        context.getAssignments().addAll(deskAssignments);
    }

    private void validateDeskIds(List<String> deskIds) {
        Objects.requireNonNull(deskIds, "Search desk IDs are required");
        if (deskIds.stream().anyMatch(id -> id == null || id.isBlank())) {
            throw new IllegalArgumentException("Search desk IDs must not contain null or blank IDs");
        }
    }

    private List<AssignmentEntry> createAssignments(List<String> deskIds) {
        List<AssignmentEntry> assignments = new ArrayList<>();
        Instant assignedAt = Instant.now();
        for (String deskId : deskIds) {
            assignments.add(createDeskAssignment(deskId, assignedAt));
        }
        return assignments;
    }

    private AssignmentEntry createDeskAssignment(String deskId, Instant assignedAt) {
        return AssignmentEntry.builder()
                .assigneeId(deskId)
                .type(AssignmentEntry.Type.DESK)
                .source(AssignmentEntry.Source.SYSTEM)
                .assignedBy(SYSTEM)
                .assignedAt(assignedAt)
                .build();
    }
}
