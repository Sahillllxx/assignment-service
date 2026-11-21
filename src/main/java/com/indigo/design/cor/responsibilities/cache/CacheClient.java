package com.indigo.design.cor.responsibilities.cache;

@FunctionalInterface
public interface CacheClient<V, R> {
    R put(String key, V value);
}
