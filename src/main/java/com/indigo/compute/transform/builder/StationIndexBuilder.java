package com.indigo.compute.transform.builder;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.stereotype.Component;
import com.indigo.compute.index.model.AssignmentCriteriaType;
import com.indigo.compute.index.model.AssignmentIndexSnapshot;
import com.indigo.compute.index.model.DeskAssignmentRule;
import com.indigo.compute.transform.model.IndexBuildContext;

@Component
public class StationIndexBuilder implements IndexBuilder {
    @Override
    public void buildIndexes(IndexBuildContext context, AssignmentIndexSnapshot target) {
        target.setStationIndexes(context.rules().stream()
                .filter(Objects::nonNull)
                .filter(rule -> AssignmentCriteriaType.STATION.matches(rule.getCriteriaType()))
                .flatMap(rule -> stations(rule).map(station -> Map.entry(station, rule)))
                .collect(Collectors.groupingBy(Map.Entry::getKey, LinkedHashMap::new,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList()))));
    }

    private Stream<String> stations(DeskAssignmentRule rule) {
        if (rule.getSelectedStations() == null) {
            return Stream.empty();
        }
        return rule.getSelectedStations().stream().filter(Objects::nonNull)
                .map(DeskAssignmentRule.StationTerminalSelection::getIata)
                .filter(Objects::nonNull).map(String::trim).filter(value -> !value.isEmpty()).distinct();
    }
}