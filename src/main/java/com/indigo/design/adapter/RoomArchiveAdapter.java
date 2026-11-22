package com.indigo.design.adapter;

import com.indigo.contexts.EventContext;
import com.indigo.model.context.RoomArchiveContext;
import com.indigo.model.event.FlightRoomArchivedEvent;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public final class RoomArchiveAdapter implements Adapter<FlightRoomArchivedEvent, EventContext<RoomArchiveContext>> {

    @Override
    public EventContext<RoomArchiveContext> adapt(FlightRoomArchivedEvent event) {
        Objects.requireNonNull(event, "Room archive event is required");
        RoomArchiveContext state = RoomArchiveContext.builder()
                .id(event.getId())
                .status(event.getStatus())
                .archivedAt(event.getArchivedAt())
                .build();
        return new EventContext<>(state);
    }
}
