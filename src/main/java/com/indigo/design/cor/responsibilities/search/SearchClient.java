package com.indigo.design.cor.responsibilities.search;

@FunctionalInterface
public interface SearchClient<I, O> {
    O search(I input);
}
