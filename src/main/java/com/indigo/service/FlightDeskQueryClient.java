package com.indigo.service;

import com.indigo.compute.query.service.DeskQueryService;
import com.indigo.design.cor.responsibilities.search.SearchClient;
import com.indigo.model.event.FlightRoomDetails;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class FlightDeskQueryClient implements SearchClient<FlightRoomDetails, List<String>> {

    private final DeskQueryService deskQueryService;

    @Override
    public List<String> search(FlightRoomDetails roomDetail) {
        return new ArrayList<>(deskQueryService.searchDesks(roomDetail));
    }
}
