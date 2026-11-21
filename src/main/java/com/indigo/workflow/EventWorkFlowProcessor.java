package com.indigo.workflow;

import com.indigo.contexts.Context;
import com.indigo.design.adapter.Adapter;
import com.indigo.design.cor.ResponsibilityChain;
import com.indigo.design.template.ProcessorTemplate;
import java.util.Objects;

public final class EventWorkFlowProcessor<I, S> extends ProcessorTemplate<I, S> {

    private final ResponsibilityChain<S> chain;

    public EventWorkFlowProcessor(Adapter<I, ? extends Context<S>> adapter, ResponsibilityChain<S> chain) {
        super(adapter);
        this.chain = Objects.requireNonNull(chain, "Event workflow chain is required");
        if (chain.isEmpty()) {
            throw new IllegalArgumentException("Event workflow chain must contain at least one responsibility");
        }
    }

    @Override
    protected ResponsibilityChain<S> responsibilityChain() {
        return chain;
    }
}
