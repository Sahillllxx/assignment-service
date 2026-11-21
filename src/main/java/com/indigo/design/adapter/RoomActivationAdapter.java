package com.indigo.design.adapter;

import com.indigo.contexts.EventContext;
import com.indigo.model.event.FlightRoomActivatedEvent;
import com.indigo.model.context.RoomActivationContext;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.ArrayList;

@Component
public final class RoomActivationAdapter implements Adapter<FlightRoomActivatedEvent, EventContext<RoomActivationContext>> {
    @Override
    public EventContext<RoomActivationContext> adapt(FlightRoomActivatedEvent event) {
        Objects.requireNonNull(event, "Room activation event is required");
        RoomActivationContext state = getContext(event);
        return new EventContext<>(state);
    }

    private static RoomActivationContext getContext(FlightRoomActivatedEvent event) {
        return RoomActivationContext.builder()
                .id(event.getId())
                .status(event.getStatus())
                .activatedAt(event.getActivatedAt())
                .roomDetail(event.getRoomDetail())
                .assignments(new ArrayList<>())
                .build();
    }
}
