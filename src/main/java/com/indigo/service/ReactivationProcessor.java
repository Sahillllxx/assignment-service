package com.indigo.service;

import com.indigo.design.cor.ChainDecision;
import com.indigo.model.FlightAssignment;
import com.indigo.model.context.RoomActivationContext;
import org.springframework.stereotype.Component;

@Component
public final class ReactivationProcessor {
    public ChainDecision process(RoomActivationContext context) {
        context.setReactivationAssignmentId(null);
        FlightAssignment assignment = context.getExistingAssignment();
        if (assignment == null || assignment.isActive()) {
            return ChainDecision.CONTINUE;
        }

        String assignmentId = assignment.getId();
        if (assignmentId == null || assignmentId.isBlank()) {
            throw new IllegalStateException("Inactive flight assignment must have an ID");
        }
        context.setReactivationAssignmentId(assignmentId);
        return ChainDecision.CONTINUE;
    }
}
