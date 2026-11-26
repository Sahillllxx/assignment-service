package com.indigo.compute.extract.model;

import java.util.Collection;

import lombok.Builder;

@Builder
public record AssignmentSourceData(Collection<DeskAssignmentConfig> configs,
        Collection<AircraftMaster> aircraftMasters) {
}