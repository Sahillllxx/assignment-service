package com.indigo.contexts;


import java.util.Objects;

public final class EventContext<S> implements Context<S> {
    private S state;
    private boolean closed;

    public EventContext(S state) {
        setState(state);
    }

    @Override
    public S getState() {
        ensureOpen();
        return state;
    }

    @Override
    public void setState(S state) {
        ensureOpen();
        this.state = Objects.requireNonNull(state, "Context state must not be null");
    }

    @Override
    public void close() {
        state = null;
        closed = true;
    }

    private void ensureOpen() {
        if (closed) {
            throw new IllegalStateException("Context is closed");
        }
    }
}
