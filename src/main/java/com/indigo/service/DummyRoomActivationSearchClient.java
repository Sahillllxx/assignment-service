package com.indigo.service;

import com.indigo.design.cor.responsibilities.search.SearchClient;
import com.indigo.model.event.FlightRoomDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class DummyRoomActivationSearchClient implements SearchClient<FlightRoomDetails, List<String>> {

    @Override
    public List<String> search(FlightRoomDetails roomDetail) {
        log.warn("Dummy room activation search returning desks for flightId={}", roomDetail.getId());
        return List.of("6a8d26945294449ee372ef2b","6aa9396eca39ed52a5c42887");
    }
}
