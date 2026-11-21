package com.indigo.design.cor.responsibilities.events.producer;

import com.indigo.contexts.Context;
import com.indigo.design.cor.ChainDecision;
import com.indigo.design.cor.Responsibility;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

public final class EventProducerResponsibility<S, I, O> implements Responsibility<S> {
    private final EventPublisher<I, O> producer;
    private final Function<S, I> eventMapper;
    private final BiConsumer<S, O> resultHandler;

    public EventProducerResponsibility(EventPublisher<I, O> producer,
                                       Function<S, I> eventMapper,
                                       BiConsumer<S, O> resultHandler) {
        this.producer = Objects.requireNonNull(producer, "producer");
        this.eventMapper = Objects.requireNonNull(eventMapper, "eventMapper");
        this.resultHandler = Objects.requireNonNull(resultHandler, "resultHandler");
    }

    @Override
    public ChainDecision handle(Context<S> context) {
        Objects.requireNonNull(context, "Producer context must not be null");
        S state = Objects.requireNonNull(context.getState(), "Producer context must contain a state");
        I event = Objects.requireNonNull(eventMapper.apply(state), "Event mapper must return an event");
        O result = Objects.requireNonNull(producer.publish(event), "Event publisher must return a result");
        resultHandler.accept(state, result);
        return ChainDecision.CONTINUE;
    }
}
