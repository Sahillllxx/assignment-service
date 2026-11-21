package com.indigo.model.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FlightRoomDetails {

    private String id;
    private String legId;
    private String carrier;
    private String dateOfOrigin;
    private Integer flightNumber;
    private String startStation;
    private String endStation;
    private String startTerminal;
    private String endTerminal;
    private String operationalStatus;
    private String flightStatus;
    private String flightType;
    private String plannedaircraftTypeIndigo;
    private String assignedAircraftTypeIndigo;
    private String assignedAircraftTypeIATA;
    private String aircraftRegistration;
    private String aircraftType;
    private String effectiveArrivalStation;
    private String effectiveArrivalTerminal;
}
