package com.indigo.design.cor.responsibilities.persistence.command;


import com.indigo.contexts.Context;
import com.indigo.design.cor.ChainDecision;
import com.indigo.design.cor.Responsibility;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

public final class PersistenceResponsibility<S, I, O> implements Responsibility<S> {
    private final PersistenceCommandClient<I, O> command;
    private final Function<S, Optional<I>> requestMapper;
    private final BiConsumer<S, O> resultHandler;

    public PersistenceResponsibility(PersistenceCommandClient<I, O> command,
                                     Function<S, Optional<I>> requestMapper,
                                     BiConsumer<S, O> resultHandler) {
        this.command = Objects.requireNonNull(command, "command");
        this.requestMapper = Objects.requireNonNull(requestMapper, "requestMapper");
        this.resultHandler = Objects.requireNonNull(resultHandler, "resultHandler");
    }

    @Override
    public ChainDecision handle(Context<S> context) {
        Objects.requireNonNull(context, "Persistence context must not be null");
        S state = Objects.requireNonNull(context.getState(), "Persistence context must contain a state");
        Optional<I> request = requestMapper.apply(state);
        if (request == null) {
            throw new IllegalStateException("Persistence request mapper must return an Optional");
        }
        if (request.isEmpty()) {
            return ChainDecision.CONTINUE;
        }
        O result = command.execute(request.get());
        if (result == null) {
            throw new IllegalStateException("Persistence command must return a non-null result");
        }
        resultHandler.accept(state, result);
        return ChainDecision.CONTINUE;
    }
}
