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
@Document(collection = "aircraft_master")
public class AircraftMaster {
    @Id
    private String id;
    private String zone;
    private List<AircraftSummary> aircraftList;
    private int aircraftCount;
    private Instant lastSyncedOn;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AircraftSummary {
        private String regNo;
        private String acType;
        private String owner;
        private String serviceStart;
        private String serviceEnd;
    }
}