package com.indigo.design.cor.responsibilities.processing;

import com.indigo.contexts.Context;
import com.indigo.design.cor.ChainDecision;
import com.indigo.design.cor.Responsibility;

import java.util.Objects;
import java.util.function.Function;

public final class ProcessingResponsibility<S> implements Responsibility<S> {
    private final Function<S, ChainDecision> processor;

    public ProcessingResponsibility(Function<S, ChainDecision> processor) {
        this.processor = Objects.requireNonNull(processor, "processor");
    }

    @Override
    public ChainDecision handle(Context<S> context) {
        Objects.requireNonNull(context, "Processing context must not be null");
        S state = Objects.requireNonNull(context.getState(), "Processing context must contain a state");
        ChainDecision decision = processor.apply(state);
        if (decision == null) {
            throw new IllegalStateException("Processor must return a chain decision");
        }
        return decision;
    }
}
