package com.indigo.compute.query.service;

import java.util.Set;

import com.indigo.model.event.FlightRoomDetails;

public interface DeskQueryService {

    /** Returns unique desk IDs matching the supplied flight room, in encounter order. */
    Set<String> searchDesks(FlightRoomDetails roomDetails);
}