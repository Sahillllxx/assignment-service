package com.indigo.workflow;

import com.indigo.design.adapter.RoomActivationAdapter;
import com.indigo.design.cor.ResponsibilityChain;
import com.indigo.model.event.FlightRoomActivatedEvent;
import com.indigo.model.context.RoomActivationContext;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class WorkflowAssembly {

    @Bean
    EventWorkFlowProcessor<FlightRoomActivatedEvent, RoomActivationContext> roomActivateEventProcessor(RoomActivationAdapter adapter,
                                                                                                       @Qualifier("roomActivatedChain")
                                                                                                   ResponsibilityChain<RoomActivationContext> chain) {
        return new EventWorkFlowProcessor<>(adapter, chain);
    }
}
