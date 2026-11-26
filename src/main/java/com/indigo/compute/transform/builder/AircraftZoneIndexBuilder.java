package com.indigo.compute.transform.builder;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import com.indigo.compute.extract.model.AircraftMaster;
import com.indigo.compute.index.model.AssignmentIndexSnapshot;
import com.indigo.compute.transform.model.IndexBuildContext;

@Component
public class AircraftZoneIndexBuilder implements IndexBuilder {
    @Override
    public void buildIndexes(IndexBuildContext context, AssignmentIndexSnapshot target) {
        target.setAircraftZoneIndexes(context.aircraftMasters().stream()
                .filter(Objects::nonNull)
                .filter(master -> StringUtils.hasText(master.getZone()))
                .flatMap(this::mapAircraftToZoneEntries)
                .collect(Collectors.groupingBy(Map.Entry::getKey, LinkedHashMap::new,
                        Collectors.mapping(Map.Entry::getValue, Collectors.collectingAndThen(
                                Collectors.toCollection(LinkedHashSet::new), ArrayList::new)))));
    }

    private Stream<Map.Entry<String, String>> mapAircraftToZoneEntries(AircraftMaster master) {
        if (master.getAircraftList() == null) {
            return Stream.empty();
        }
        String zone = master.getZone().trim();
        return master.getAircraftList().stream().filter(Objects::nonNull)
                .map(AircraftMaster.AircraftSummary::getRegNo)
                .filter(Objects::nonNull).map(String::trim).filter(registration -> !registration.isEmpty())
                .map(registration -> Map.entry(registration, zone));
    }
}