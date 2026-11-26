package com.indigo.compute.extract;

import java.util.Collection;

public interface SourceReader<T> {
    Collection<T> read();
}
