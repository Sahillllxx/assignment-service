package com.indigo.design.cor;

import com.indigo.contexts.Context;
import com.indigo.design.ProcessingResult;

import java.util.List;
import java.util.Objects;

public final class ResponsibilityChain<S> {
    private final List<Responsibility<S>> responsibilities;

    public ResponsibilityChain(List<? extends Responsibility<S>> responsibilities) {
        this.responsibilities = List.copyOf(responsibilities);
    }

    public boolean isEmpty() {
        return responsibilities.isEmpty();
    }

    public ProcessingResult<S> execute(Context<S> context) {
        Objects.requireNonNull(context, "Context must not be null");
        Objects.requireNonNull(context.getState(), "Context must contain a state");
        for (Responsibility<S> responsibility : responsibilities) {
            if (executeResponsibility(context, responsibility) == ChainDecision.STOP) {
                return new ProcessingResult<>(context.getState(), ChainDecision.STOP);
            }
        }
        return new ProcessingResult<>(context.getState(), ChainDecision.CONTINUE);
    }

    private ChainDecision executeResponsibility(Context<S> context, Responsibility<S> responsibility) {
        ChainDecision decision;
        try {
            decision = responsibility.handle(context);
        } catch (RuntimeException exception) {
            throw new IllegalStateException("Responsibility " + responsibility.getClass().getName()
                    + " threw an exception", exception);
        }
        if (decision == null) {
            throw new IllegalStateException("Responsibility " + responsibility.getClass().getName()
                    + " must return a chain decision");
        }
        return decision;
    }
}
