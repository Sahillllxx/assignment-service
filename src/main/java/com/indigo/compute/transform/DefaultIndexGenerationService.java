package com.indigo.compute.transform;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import com.indigo.compute.extract.model.DeskAssignmentConfig;
import com.indigo.compute.extract.model.AssignmentSourceData;
import com.indigo.compute.index.model.AssignmentIndexSnapshot;
import com.indigo.compute.index.model.DeskAssignmentRule;
import com.indigo.compute.transform.builder.IndexBuilder;
import com.indigo.compute.transform.model.IndexBuildContext;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DefaultIndexGenerationService implements IndexGenerationService {
    private final List<IndexBuilder> indexBuilders;

    @Override
    public AssignmentIndexSnapshot generate(AssignmentSourceData data) {
        List<DeskAssignmentRule> rules = data.configs().stream()
                .map(DefaultIndexGenerationService::toRule).toList();
        IndexBuildContext context = new IndexBuildContext(rules, data.aircraftMasters());
        AssignmentIndexSnapshot snapshot = new AssignmentIndexSnapshot();
        indexBuilders.forEach(builder -> builder.buildIndexes(context, snapshot));
        return snapshot;
    }

    /** Copy every source field without normalizing the persisted rule data. */
    public static DeskAssignmentRule toRule(DeskAssignmentConfig source) {
        if (source == null) {
            return null;
        }
        return DeskAssignmentRule.builder()
                .id(source.getId()).deskId(source.getDeskId()).deskName(source.getDeskName())
                .subDepartmentId(source.getSubDepartmentId()).criteriaType(source.getCriteriaType())
                .selectedZones(copy(source.getSelectedZones()))
                .selectedStations(source.getSelectedStations() == null ? null : new ArrayList<>(
                        source.getSelectedStations().stream().map(station -> station == null ? null :
                                DeskAssignmentRule.StationTerminalSelection.builder()
                                        .iata(station.getIata()).stationName(station.getStationName())
                                        .terminalNames(copy(station.getTerminalNames())).build()).toList()))
                .selectedTails(copy(source.getSelectedTails()))
                .selectedAircraftTypes(copy(source.getSelectedAircraftTypes()))
                .selectedFlightTypes(copy(source.getSelectedFlightTypes()))
                .isActive(source.isActive()).isDeleted(source.isDeleted())
                .createdBy(source.getCreatedBy()).createdAt(source.getCreatedAt())
                .updatedBy(source.getUpdatedBy()).updatedAt(source.getUpdatedAt()).build();
    }

    private static <T> List<T> copy(List<T> values) {
        return values == null ? null : new ArrayList<>(values);
    }
}