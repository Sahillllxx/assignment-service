package com.indigo.workflow.validations;

import com.indigo.design.cor.responsibilities.validation.ValidationResult;
import com.indigo.design.validation.ValidationRule;
import com.indigo.design.validation.Validator;
import com.indigo.model.context.RoomArchiveContext;
import org.springframework.stereotype.Component;

@Component
public final class RoomArchiveValidator implements Validator<RoomArchiveContext> {
    private final ValidationRule<RoomArchiveContext> rules = RoomArchiveRules.contextNotNull()
            .and(RoomArchiveRules.idRequired());

    @Override
    public ValidationResult isValid(RoomArchiveContext input) {
        return rules.apply(input);
    }
}
