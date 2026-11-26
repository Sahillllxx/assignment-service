package com.indigo.compute.query.service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.util.function.SingletonSupplier;
import com.indigo.compute.index.model.AssignmentIndexSnapshot;
import com.indigo.compute.index.model.DeskAssignmentRule;
import com.indigo.compute.query.reader.AssignmentIndexReader;
import com.indigo.compute.query.strategy.DeskMatchStrategy;
import com.indigo.model.event.FlightRoomDetails;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DefaultDeskQueryService implements DeskQueryService {
    private final AssignmentIndexReader indexReader;
    private final List<DeskMatchStrategy> strategies;

    @Override
    public Set<String> searchDesks(FlightRoomDetails roomDetails) {
        if (Objects.isNull(roomDetails)) {
            return Set.of();
        }
        SingletonSupplier<AssignmentIndexSnapshot> indexes = SingletonSupplier.of(() -> indexReader.readIndexes(roomDetails));
        return strategies.stream()
            .filter(strategy -> strategy.supports(roomDetails))
            .flatMap(strategy -> strategy.match(roomDetails, indexes.get()).stream())
                .filter(Objects::nonNull)
                .map(DeskAssignmentRule::getDeskId)
                .filter(StringUtils::hasText)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}