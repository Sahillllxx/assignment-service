package com.indigo.model.event;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FlightRoomActivatedEvent {

    private String id;
    private String status;
    private Instant activatedAt;

    @JsonAlias("roomDetails")
    private FlightRoomDetails roomDetail;
}
