package com.indigo.design.validation;


import com.indigo.design.cor.responsibilities.validation.ValidationResult;

import java.util.Objects;
import java.util.function.Function;

@FunctionalInterface
public interface ValidationRule<T> extends Function<T, ValidationResult> {

    default ValidationRule<T> and(ValidationRule<T> other) {
        Objects.requireNonNull(other, "other");
        return input -> {
            ValidationResult result = getResult(apply(input));
            if (!result.valid()) {
                return result;
            }
            return getResult(other.apply(input));
        };
    }

    private static ValidationResult getResult(ValidationResult result) {
        if (result == null) {
            throw new IllegalStateException("Input validator must return a validation result");
        }
        return result;
    }
}
