package com.indigo.compute.index.model;

import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Complete persisted rule data, independent of source documents and Mongo mapping. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeskAssignmentRule {
    private String id;
    private String deskId;
    private String deskName;
    private String subDepartmentId;
    private String criteriaType;
    private List<String> selectedZones;
    private List<StationTerminalSelection> selectedStations;
    private List<String> selectedTails;
    private List<String> selectedAircraftTypes;
    private List<String> selectedFlightTypes;
    @Builder.Default
    private boolean isActive = true;
    @Builder.Default
    private boolean isDeleted = false;
    private String createdBy;
    private Instant createdAt;
    private String updatedBy;
    private Instant updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StationTerminalSelection {
        private String iata;
        private String stationName;
        private List<String> terminalNames;
    }
}