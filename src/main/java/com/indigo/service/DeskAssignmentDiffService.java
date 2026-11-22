package com.indigo.service;

import com.indigo.design.cor.ChainDecision;
import com.indigo.model.FlightAssignment;
import com.indigo.model.context.DeskAssignmentDiff;
import com.indigo.model.context.RoomActivationContext;
import com.indigo.model.event.AssignmentEntry;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
public final class DeskAssignmentDiffService {
    private static final String DESK = "DESK";
    private static final String SYSTEM = "SYSTEM";
    private static final String MANUAL = "MANUAL";

    public ChainDecision process(RoomActivationContext context) {
        Objects.requireNonNull(context, "Room activation context is required");
        FlightAssignment existing = context.getExistingAssignment();
        Set<String> matchedDesks = new LinkedHashSet<>(Objects.requireNonNull(context.getAssignedDeskIds(), "Search desk IDs are required"));
        List<FlightAssignment.AssignmentEntry> existingEntries = existingEntries(existing);
        Set<String> oldDesks = existingDeskIds(existing);
        Set<String> manualDesks = manualDeskIds(existingEntries);
        Set<String> finalDesks = new LinkedHashSet<>(matchedDesks);
        finalDesks.addAll(manualDesks);
        Set<String> addedDesks = new LinkedHashSet<>(matchedDesks);
        addedDesks.removeAll(oldDesks);
        Set<String> removedDesks = removedDeskIds(oldDesks, existingEntries, matchedDesks, manualDesks);
        if (context.isWasReactivated()) {
            addedDesks.addAll(finalDesks);
        }

        boolean hasChanges = !addedDesks.isEmpty() || !removedDesks.isEmpty();
        Instant now = Instant.now();
        List<FlightAssignment.AssignmentEntry> entries = buildEntries(
                existingEntries, context.getAssignments(), oldDesks, removedDesks);
        FlightAssignment assignment = buildAssignment(context, existing, finalDesks, entries, hasChanges, now);
        context.setDeskAssignmentDiff(DeskAssignmentDiff.builder()
                .assignment(assignment)
                .addedDeskIds(List.copyOf(addedDesks))
                .removedDeskIds(List.copyOf(removedDesks))
                .finalDeskIds(List.copyOf(finalDesks))
                .hasChanges(hasChanges)
                .build());
        return ChainDecision.CONTINUE;
    }

    private List<FlightAssignment.AssignmentEntry> existingEntries(FlightAssignment assignment) {
        if (assignment == null || assignment.getAssignments() == null) {
            return List.of();
        }
        return assignment.getAssignments();
    }

    private Set<String> existingDeskIds(FlightAssignment assignment) {
        if (assignment == null || assignment.getAssignedDeskIds() == null) {
            return new LinkedHashSet<>();
        }
        return new LinkedHashSet<>(assignment.getAssignedDeskIds());
    }

    private Set<String> manualDeskIds(List<FlightAssignment.AssignmentEntry> entries) {
        Set<String> manualDesks = new LinkedHashSet<>();
        for (FlightAssignment.AssignmentEntry entry : entries) {
            if (isDeskEntry(entry, MANUAL) && hasId(entry.getAssigneeId())) {
                manualDesks.add(entry.getAssigneeId());
            }
        }
        return manualDesks;
    }

    private Set<String> removedDeskIds(Set<String> oldDesks, List<FlightAssignment.AssignmentEntry> entries,
                                       Set<String> matchedDesks, Set<String> manualDesks) {
        Set<String> systemDesks = new LinkedHashSet<>(oldDesks);
        systemDesks.removeAll(manualDesks);
        for (FlightAssignment.AssignmentEntry entry : entries) {
            if (isDeskEntry(entry, SYSTEM) && hasId(entry.getAssigneeId())) {
                systemDesks.add(entry.getAssigneeId());
            }
        }
        systemDesks.removeAll(matchedDesks);
        systemDesks.removeAll(manualDesks);
        return systemDesks;
    }

    private List<FlightAssignment.AssignmentEntry> buildEntries(
            List<FlightAssignment.AssignmentEntry> existingEntries, List<AssignmentEntry> searchEntries,
            Set<String> oldDesks, Set<String> removedDesks) {
        Objects.requireNonNull(searchEntries, "Search assignment entries are required");
        List<FlightAssignment.AssignmentEntry> result = new ArrayList<>();
        Set<String> recordedDesks = new HashSet<>(oldDesks);

        for (FlightAssignment.AssignmentEntry entry : existingEntries) {
            if (isDeskEntry(entry, SYSTEM) && removedDesks.contains(entry.getAssigneeId())) {
                continue;
            }
            result.add(entry);
            if (entry != null && DESK.equals(entry.getType()) && hasId(entry.getAssigneeId())) {
                recordedDesks.add(entry.getAssigneeId());
            }
        }
        for (AssignmentEntry entry : searchEntries) {
            Objects.requireNonNull(entry, "Search assignment entry must not be null");
            if (entry.getType() != AssignmentEntry.Type.DESK || entry.getSource() != AssignmentEntry.Source.SYSTEM
                    || !hasId(entry.getAssigneeId())) {
                throw new IllegalStateException("Search must provide valid SYSTEM desk assignment entries");
            }
            if (recordedDesks.add(entry.getAssigneeId())) {
                result.add(FlightAssignment.AssignmentEntry.builder()
                        .assigneeId(entry.getAssigneeId())
                        .type(DESK)
                        .source(SYSTEM)
                        .assignedBy(entry.getAssignedBy())
                        .assignedAt(entry.getAssignedAt())
                        .build());
            }
        }
        return result;
    }

    private FlightAssignment buildAssignment(RoomActivationContext context, FlightAssignment existing,
                                             Set<String> finalDesks, List<FlightAssignment.AssignmentEntry> entries,
                                             boolean hasChanges, Instant now) {
        if (existing == null) {
            return FlightAssignment.builder()
                    .flightId(context.requireFlightId())
                    .assignedDeskIds(new ArrayList<>(finalDesks))
                    .assignments(entries)
                    .isActive(true)
                    .createdAt(now)
                    .updatedAt(now)
                    .build();
        }
        return FlightAssignment.builder()
                .id(existing.getId())
                .flightId(existing.getFlightId())
                .assignedDeskIds(new ArrayList<>(finalDesks))
                .assignedUserIds(existing.getAssignedUserIds())
                .assignments(entries)
                .isActive(existing.isActive())
                .createdAt(existing.getCreatedAt())
                .updatedAt(hasChanges ? now : existing.getUpdatedAt())
                .build();
    }

    private boolean isDeskEntry(FlightAssignment.AssignmentEntry entry, String source) {
        return entry != null && DESK.equals(entry.getType()) && source.equals(entry.getSource());
    }

    private boolean hasId(String id) {
        return id != null && !id.isBlank();
    }
}
