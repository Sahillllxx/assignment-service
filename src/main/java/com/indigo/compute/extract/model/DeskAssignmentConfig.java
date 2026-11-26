package com.indigo.compute.extract.model;

import java.time.Instant;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "desk_assignments_config")
public class DeskAssignmentConfig {
    @Id
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