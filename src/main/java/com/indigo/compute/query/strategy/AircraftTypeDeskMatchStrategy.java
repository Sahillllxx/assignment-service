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
@Order(3)
public class AircraftTypeDeskMatchStrategy implements DeskMatchStrategy {
    @Override
    public boolean supports(FlightRoomDetails room) {
        return room != null && StringUtils.hasText(room.getAircraftType());
    }

    @Override
    public List<DeskAssignmentRule> match(FlightRoomDetails room, AssignmentIndexSnapshot indexes) {
        if (!supports(room) || indexes == null) {
            return List.of();
        }
        List<DeskAssignmentRule> matches = new ArrayList<>();
        for (DeskAssignmentRule rule : QueryIndexLookup.values(indexes.getAircraftTypeIndexes(), room.getAircraftType())) {
            if (rule != null && flightTypeMatches(rule.getSelectedFlightTypes(), room.getFlightType())) {
                matches.add(rule);
            }
        }
        return matches;
    }

    private boolean flightTypeMatches(List<String> selected, String flightType) {
        if (selected == null || selected.isEmpty()) {
            return true;
        }
        if (!StringUtils.hasText(flightType)) {
            return false;
        }
        return selected.stream().anyMatch(value -> value != null && value.trim().equalsIgnoreCase(flightType.trim()));
    }
}