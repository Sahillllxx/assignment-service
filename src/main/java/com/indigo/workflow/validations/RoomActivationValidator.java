package com.indigo.workflow.validations;

import com.indigo.design.cor.responsibilities.validation.ValidationResult;
import com.indigo.design.validation.ValidationRule;
import com.indigo.design.validation.Validator;
import com.indigo.model.context.RoomActivationContext;
import org.springframework.stereotype.Component;

@Component
public final class RoomActivationValidator implements Validator<RoomActivationContext> {
    private final ValidationRule<RoomActivationContext> rules = RoomActivationRules.contextNotNull()
            .and(RoomActivationRules.idRequired())
            .and(RoomActivationRules.statusRequired())
            .and(RoomActivationRules.activatedAtRequired())
            .and(RoomActivationRules.roomDetailRequired());

    @Override
    public ValidationResult isValid(RoomActivationContext input) {
        return rules.apply(input);
    }
}
