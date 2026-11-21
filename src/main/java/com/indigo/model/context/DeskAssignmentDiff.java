package com.indigo.model.context;

import com.indigo.model.FlightAssignment;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class DeskAssignmentDiff {
    FlightAssignment assignment;
    List<String> addedDeskIds;
    List<String> removedDeskIds;
    List<String> finalDeskIds;
    boolean hasChanges;
}
