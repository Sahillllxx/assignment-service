package com.indigo.design.cor.responsibilities.events.producer;

@FunctionalInterface
public interface EventPublisher<I, O> {
    O publish(I input);
}
