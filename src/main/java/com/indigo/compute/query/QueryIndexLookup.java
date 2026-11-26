package com.indigo.compute.query;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.util.StringUtils;

/** Direct reverse-index lookups; never scan indexes to compensate for unnormalized writes. */
public final class QueryIndexLookup {
    private QueryIndexLookup() {
    }

    public static <T> List<T> values(Map<String, List<T>> index, String key) {
        if (index == null || !StringUtils.hasText(key)) {
            return List.of();
        }
        String trimmed = key.trim();
        List<T> values = index.get(trimmed.toUpperCase(Locale.ROOT));
        if (values == null) {
            values = index.get(trimmed);
        }
        return values == null ? List.of() : values;
    }
}