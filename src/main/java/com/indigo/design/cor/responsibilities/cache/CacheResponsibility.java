package com.indigo.design.cor.responsibilities.cache;


import com.indigo.contexts.Context;
import com.indigo.design.cor.ChainDecision;
import com.indigo.design.cor.Responsibility;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

public final class CacheResponsibility<S, V, R> implements Responsibility<S> {
    private final CacheClient<V, R> cache;
    private final Function<S, String> keyMapper;
    private final Function<S, V> valueMapper;
    private final BiConsumer<S, R> resultHandler;

    public CacheResponsibility(CacheClient<V, R> cache, Function<S, String> keyMapper,
                               Function<S, V> valueMapper, BiConsumer<S, R> resultHandler) {
        this.cache = Objects.requireNonNull(cache, "cache");
        this.keyMapper = Objects.requireNonNull(keyMapper, "keyMapper");
        this.valueMapper = Objects.requireNonNull(valueMapper, "valueMapper");
        this.resultHandler = Objects.requireNonNull(resultHandler, "resultHandler");
    }

    @Override
    public ChainDecision handle(Context<S> context) {
        Objects.requireNonNull(context, "Cache context must not be null");
        S state = Objects.requireNonNull(context.getState(), "Cache context must contain a state");
        String key = keyMapper.apply(state);
        if (key == null || key.isBlank()) {
            throw new IllegalStateException("Cache key must not be blank");
        }
        V value = Objects.requireNonNull(valueMapper.apply(state), "Cache value must not be null");
        R result = Objects.requireNonNull(cache.put(key, value), "Cache result must not be null");
        resultHandler.accept(state, result);
        return ChainDecision.CONTINUE;
    }
}
