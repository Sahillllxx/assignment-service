package com.indigo.design.cor.responsibilities.persistence.command;

@FunctionalInterface
public interface PersistenceCommandClient<I, O> {
    O execute(I input);
}
