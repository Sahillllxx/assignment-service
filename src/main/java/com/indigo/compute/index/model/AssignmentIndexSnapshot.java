package com.indigo.compute.index.model;

import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AssignmentIndexSnapshot {
    private Map<String, List<DeskAssignmentRule>> stationIndexes;
    private Map<String, List<DeskAssignmentRule>> aircraftRegistrationIndexes;
    private Map<String, List<DeskAssignmentRule>> aircraftTypeIndexes;
    private Map<String, List<DeskAssignmentRule>> zoneIndexes;
    private Map<String, List<String>> aircraftZoneIndexes;
}