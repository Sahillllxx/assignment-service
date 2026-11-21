package com.indigo.design.validation;

import com.indigo.design.cor.responsibilities.validation.ValidationResult;

@FunctionalInterface
public interface Validator<T> {
    ValidationResult isValid(T input);
}
