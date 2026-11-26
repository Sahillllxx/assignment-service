package com.indigo.compute.transform.model;

import java.util.Collection;
import com.indigo.compute.index.model.DeskAssignmentRule;
import com.indigo.compute.extract.model.AircraftMaster;

/** In-memory inputs shared by all builders during one generation. */
public record IndexBuildContext(Collection<DeskAssignmentRule> rules,
        Collection<AircraftMaster> aircraftMasters) {
}