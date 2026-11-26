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
public class AircraftTypeIndexBuilder implements IndexBuilder {
    @Override
    public void buildIndexes(IndexBuildContext context, AssignmentIndexSnapshot target) {
        target.setAircraftTypeIndexes(context.rules().stream()
                .filter(Objects::nonNull)
                .filter(rule -> AssignmentCriteriaType.AIRCRAFT_TYPE.matches(rule.getCriteriaType()))
                .flatMap(rule -> types(rule).map(type -> Map.entry(type, rule)))
                .collect(Collectors.groupingBy(Map.Entry::getKey, LinkedHashMap::new,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList()))));
    }

    private Stream<String> types(DeskAssignmentRule rule) {
        if (rule.getSelectedAircraftTypes() == null) {
            return Stream.empty();
        }
        return rule.getSelectedAircraftTypes().stream().filter(Objects::nonNull).map(String::trim)
                .filter(value -> !value.isEmpty()).distinct();
    }
}