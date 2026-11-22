package com.indigo.service;

import com.indigo.design.cor.ChainDecision;
import com.indigo.model.FlightAssignment;
import com.indigo.model.context.RoomArchiveContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public final class ArchiveProcessor {

    public ChainDecision process(RoomArchiveContext context) {
        Objects.requireNonNull(context, "Room archive context is required");
        FlightAssignment assignment = context.getExistingAssignment();
        if (assignment == null) {
            log.info("No flight assignment found for flightId={}, nothing to archive", context.getId());
            return ChainDecision.STOP;
        }
        if (!assignment.isActive()) {
            log.info("Flight assignment is already inactive for flightId={}, skipping archive", context.getId());
            return ChainDecision.STOP;
        }

        context.setPreviousDeskIds(copyIds(assignment.getAssignedDeskIds()));
        context.setPreviousUserIds(copyIds(assignment.getAssignedUserIds()));
        assignment.setActive(false);
        assignment.setUpdatedAt(Instant.now());
        return ChainDecision.CONTINUE;
    }

    private List<String> copyIds(List<String> ids) {
        return ids == null ? List.of() : new ArrayList<>(ids);
    }
}
