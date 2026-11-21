package com.indigo.design.template;

import com.indigo.contexts.Context;
import com.indigo.design.ProcessingResult;
import com.indigo.design.adapter.Adapter;
import com.indigo.design.cor.ResponsibilityChain;

import java.util.Objects;

public abstract class ProcessorTemplate<I, S> {

    private final Adapter<I, ? extends Context<S>> adapter;

    protected ProcessorTemplate(Adapter<I, ? extends Context<S>> adapter) {
        this.adapter = Objects.requireNonNull(adapter, "adapter");
    }

    public final ProcessingResult<S> process(I input) {
        Context<S> adapted = adapter.adapt(input);
        if (Objects.isNull(adapted)) {
            throw new IllegalStateException("Adapter must return a non-null context");
        }
        try (Context<S> context = adapted) {
            Objects.requireNonNull(context.getState(), "Context must contain a state");
            ResponsibilityChain<S> chain = responsibilityChain();
            if (Objects.isNull(chain)) {
                throw new IllegalStateException("Processor must provide a responsibility chain");
            }
            return chain.execute(context);
        }
    }

    protected abstract ResponsibilityChain<S> responsibilityChain();
}
