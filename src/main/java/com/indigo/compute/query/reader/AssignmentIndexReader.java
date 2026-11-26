package com.indigo.compute.query.reader;

import com.indigo.compute.index.model.AssignmentIndexSnapshot;
import com.indigo.model.event.FlightRoomDetails;

public interface AssignmentIndexReader {
    /** Read only indexes needed by the supplied flight room; missing keys are empty maps. */
    AssignmentIndexSnapshot readIndexes(FlightRoomDetails room);
}