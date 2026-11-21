package com.indigo.design.cor;

import com.indigo.contexts.Context;

@FunctionalInterface
public interface Responsibility<S> {
    ChainDecision handle(Context<S> context);
}
