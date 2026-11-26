package com.indigo.compute.query.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import com.indigo.compute.index.model.AssignmentIndexSnapshot;
import com.indigo.compute.index.model.DeskAssignmentRule;
import com.indigo.compute.index.model.DeskAssignmentRule.StationTerminalSelection;
import com.indigo.compute.query.QueryIndexLookup;
import com.indigo.model.event.FlightRoomDetails;

@Component
@Order(1)
public class StationDeskMatchStrategy implements DeskMatchStrategy {
    @Override
    public boolean supports(FlightRoomDetails room) {
        return room != null && (StringUtils.hasText(room.getStartStation()) || StringUtils.hasText(room.getEndStation()));
    }

    @Override
    public List<DeskAssignmentRule> match(FlightRoomDetails room, AssignmentIndexSnapshot indexes) {
        if (!supports(room) || indexes == null) {
            return List.of();
        }
        List<DeskAssignmentRule> matches = new ArrayList<>();
        addMatches(matches, indexes, room.getStartStation(), room.getStartTerminal());
        // Preserve the legacy start-first terminal choice when both endpoints are the same station.
        String endTerminal = equalStation(room.getStartStation(), room.getEndStation())
                ? room.getStartTerminal() : room.getEndTerminal();
        addMatches(matches, indexes, room.getEndStation(), endTerminal);
        return matches;
    }

    private void addMatches(List<DeskAssignmentRule> matches, AssignmentIndexSnapshot indexes,
            String station, String terminal) {
        for (DeskAssignmentRule rule : QueryIndexLookup.values(indexes.getStationIndexes(), station)) {
            if (rule == null || rule.getSelectedStations() == null) {
                continue;
            }
            for (StationTerminalSelection selection : rule.getSelectedStations()) {
                if (selection != null && equalStation(selection.getIata(), station)
                        && terminalMatches(selection.getTerminalNames(), terminal)) {
                    matches.add(rule);
                    break;
                }
            }
        }
    }

    private boolean equalStation(String configured, String station) {
        return StringUtils.hasText(configured) && StringUtils.hasText(station)
                && configured.trim().equalsIgnoreCase(station.trim());
    }

    private boolean terminalMatches(List<String> configured, String terminal) {
        if (configured == null || configured.isEmpty() || !StringUtils.hasText(terminal)) {
            return true;
        }
        String target = normalizeTerminal(terminal);
        for (String value : configured) {
            if (value != null && (value.trim().equalsIgnoreCase(terminal.trim())
                    || (!target.isEmpty() && normalizeTerminal(value).equals(target)))) {
                return true;
            }
        }
        return false;
    }

    private String normalizeTerminal(String terminal) {
        return terminal.toUpperCase(Locale.ROOT).replaceAll("[^A-Z0-9]", "").replace("T", "");
    }
}