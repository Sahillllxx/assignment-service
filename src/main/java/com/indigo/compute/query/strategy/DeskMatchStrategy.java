package com.indigo.compute.query.strategy;

import java.util.List;
import com.indigo.compute.index.model.AssignmentIndexSnapshot;
import com.indigo.compute.index.model.DeskAssignmentRule;
import com.indigo.model.event.FlightRoomDetails;

/** Pure matching over a request's already loaded indexes. */
public interface DeskMatchStrategy {
    boolean supports(FlightRoomDetails room);

    List<DeskAssignmentRule> match(FlightRoomDetails room, AssignmentIndexSnapshot indexes);
}