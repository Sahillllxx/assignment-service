package com.indigo.design.cor.responsibilities.persistence.query;


import com.indigo.contexts.Context;
import com.indigo.design.cor.ChainDecision;
import com.indigo.design.cor.Responsibility;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

public final class PersistenceQueryResponsibility<S, I, O> implements Responsibility<S> {
    private final PersistenceQueryClient<I, O> query;
    private final Function<S, I> requestMapper;
    private final BiConsumer<S, O> resultHandler;

    public PersistenceQueryResponsibility(PersistenceQueryClient<I, O> query,
                                          Function<S, I> requestMapper,
                                          BiConsumer<S, O> resultHandler) {
        this.query = Objects.requireNonNull(query, "query");
        this.requestMapper = Objects.requireNonNull(requestMapper, "requestMapper");
        this.resultHandler = Objects.requireNonNull(resultHandler, "resultHandler");
    }

    @Override
    public ChainDecision handle(Context<S> context) {
        Objects.requireNonNull(context, "Query context must not be null");
        S state = Objects.requireNonNull(context.getState(), "Query context must contain a state");
        I request = requestMapper.apply(state);
        if (request == null) {
            throw new IllegalStateException("Query request mapper must return a non-null request");
        }
        O result = query.query(request);
        if (result == null) {
            throw new IllegalStateException("Persistence query must return a non-null result");
        }
        resultHandler.accept(state, result);
        return ChainDecision.CONTINUE;
    }
}
