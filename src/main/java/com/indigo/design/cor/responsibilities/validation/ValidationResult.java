package com.indigo.design.cor.responsibilities.validation;

import java.util.List;

public record ValidationResult(List<String> validations) {
    public ValidationResult {
        validations = List.copyOf(validations);
    }

    public boolean valid() {
        return validations.isEmpty();
    }
}
