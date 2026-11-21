package com.indigo.contexts;

public interface Context<S> extends AutoCloseable {
    S getState();

    void setState(S state);

    /**
     * Releases context-owned references without clearing the returned state itself.
     * Implementations must allow repeated closure and reject state access afterward.
     */
    @Override
    void close();
}
