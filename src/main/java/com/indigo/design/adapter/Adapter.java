package com.indigo.design.adapter;

@FunctionalInterface
public interface Adapter<I, O> {
    O adapt(I input);
}
