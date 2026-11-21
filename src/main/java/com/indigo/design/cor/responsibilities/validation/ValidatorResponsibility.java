package com.indigo.design.cor.responsibilities.validation;

import com.indigo.contexts.Context;
import com.indigo.design.cor.ChainDecision;
import com.indigo.design.cor.Responsibility;
import com.indigo.design.validation.Validator;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public final class ValidatorResponsibility<S> implements Responsibility<S> {

    private final Validator<S> validator;

    public ValidatorResponsibility(Validator<S> validator) {
        this.validator = Objects.requireNonNull(validator, "validator");
    }

    @Override
    public ChainDecision handle(Context<S> context) {

        Objects.requireNonNull(context, "Validation context must not be null");
        ValidationResult result = validator.isValid(context.getState());
        if (result == null) {
            throw new IllegalStateException("Validator must return a validation result");
        }
        if (!result.valid()) {
            log.warn("Workflow validation failed: {}", result.validations());
            return ChainDecision.STOP;
        }
        return ChainDecision.CONTINUE;
    }
}
