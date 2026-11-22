package com.indigo.workflow.validations;

import com.indigo.design.cor.responsibilities.validation.ValidationResult;
import com.indigo.design.validation.ValidationRule;
import com.indigo.model.context.RoomActivationContext;

import java.util.List;
import java.util.Objects;

@FunctionalInterface
public interface RoomActivationRules extends ValidationRule<RoomActivationContext> {
    static RoomActivationRules contextNotNull() {
        return context -> result(Objects.nonNull(context), "Room activation context must not be null");
    }

    static RoomActivationRules idRequired() {
        return context -> result(Objects.nonNull(context) && Objects.nonNull(context.getId()) && !context.getId().isBlank(),
                "Room activation id must not be blank");
    }

    static RoomActivationRules statusRequired() {
        return context -> result(Objects.nonNull(context) && Objects.nonNull(context.getStatus()) && !context.getStatus().isBlank(),
                "Room activation status must not be blank");
    }

    static RoomActivationRules activatedAtRequired() {
        return context -> result(Objects.nonNull(context) && Objects.nonNull(context.getActivatedAt()),
                "Room activation timestamp must not be null");
    }

    static RoomActivationRules roomDetailRequired() {
        return context -> result(Objects.nonNull(context) && Objects.nonNull(context.getRoomDetail()),
                "Room details must not be null");
    }

    private static ValidationResult result(boolean valid, String message) {
        return new ValidationResult(valid ? List.of() : List.of(message));
    }
}
