package com.indigo.compute.query.strategy;

import java.util.ArrayList;
import java.util.List;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import com.indigo.compute.index.model.AssignmentIndexSnapshot;
import com.indigo.compute.index.model.DeskAssignmentRule;
import com.indigo.compute.query.QueryIndexLookup;
import com.indigo.model.event.FlightRoomDetails;

@Component
@Order(2)
public class AircraftRegistrationDeskMatchStrategy implements DeskMatchStrategy {
    @Override
    public boolean supports(FlightRoomDetails room) {
        return room != null && StringUtils.hasText(room.getAircraftRegistration());
    }

    @Override
    public List<DeskAssignmentRule> match(FlightRoomDetails room, AssignmentIndexSnapshot indexes) {
        if (!supports(room) || indexes == null) {
            return List.of();
        }
        String registration = room.getAircraftRegistration().trim();
        List<DeskAssignmentRule> matches = new ArrayList<>();
        addRules(matches, QueryIndexLookup.values(indexes.getAircraftRegistrationIndexes(), registration));
        for (String zone : QueryIndexLookup.values(indexes.getAircraftZoneIndexes(), registration)) {
            addRules(matches, QueryIndexLookup.values(indexes.getZoneIndexes(), zone));
        }
        return matches;
    }

    private void addRules(List<DeskAssignmentRule> matches, List<DeskAssignmentRule> rules) {
        for (DeskAssignmentRule rule : rules) {
            if (rule != null) {
                matches.add(rule);
            }
        }
    }
}