package com.indigo.design.cor.responsibilities.persistence.query;

@FunctionalInterface
public interface PersistenceQueryClient<I, O> {
    O query(I input);
}
