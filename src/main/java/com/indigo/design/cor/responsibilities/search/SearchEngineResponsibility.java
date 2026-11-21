package com.indigo.design.cor.responsibilities.search;


import com.indigo.contexts.Context;
import com.indigo.design.cor.ChainDecision;
import com.indigo.design.cor.Responsibility;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

public final class SearchEngineResponsibility<S, I, O> implements Responsibility<S> {
    private final SearchClient<I, O> searchEngine;
    private final Function<S, I> requestMapper;
    private final BiConsumer<S, O> resultHandler;

    public SearchEngineResponsibility(SearchClient<I, O> searchEngine, Function<S, I> requestMapper, BiConsumer<S, O> resultHandler) {
        this.searchEngine = Objects.requireNonNull(searchEngine, "searchEngine");
        this.requestMapper = Objects.requireNonNull(requestMapper, "requestMapper");
        this.resultHandler = Objects.requireNonNull(resultHandler, "resultHandler");
    }

    @Override
    public ChainDecision handle(Context<S> context) {
        Objects.requireNonNull(context, "Search context must not be null");
        S state = Objects.requireNonNull(context.getState(), "Search context must contain a state");
        I request = requestMapper.apply(state);
        if (request == null) {
            throw new IllegalStateException("Search request mapper must return a non-null request");
        }
        O result = searchEngine.search(request);
        if (result == null) {
            throw new IllegalStateException("Search client must return a non-null result");
        }
        resultHandler.accept(state, result);
        return ChainDecision.CONTINUE;
    }
}
