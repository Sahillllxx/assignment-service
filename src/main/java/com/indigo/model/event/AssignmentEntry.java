package com.indigo.model.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentEntry {

    private String assigneeId;
    private Type type;
    private Source source;
    private String assignedBy;
    private Instant assignedAt;

    public enum Type {
        USER, DESK
    }

    public enum Source {
        SYSTEM, ADMIN, MANUAL
    }
}