package com.indigo.design.validation;

import com.indigo.design.cor.responsibilities.validation.ValidationResult;
import com.indigo.model.context.RoomActivationContext;

import java.util.List;

@FunctionalInterface
public interface RoomActivationRules extends ValidationRule<RoomActivationContext> {
    static RoomActivationRules contextNotNull() {
        return context -> result(context != null, "Room activation context must not be null");
    }

    static RoomActivationRules idRequired() {
        return context -> result(context != null && context.getId() != null && !context.getId().isBlank(),
                "Room activation id must not be blank");
    }

    static RoomActivationRules statusRequired() {
        return context -> result(context != null && context.getStatus() != null && !context.getStatus().isBlank(),
                "Room activation status must not be blank");
    }

    static RoomActivationRules activatedAtRequired() {
        return context -> result(context != null && context.getActivatedAt() != null,
                "Room activation timestamp must not be null");
    }

    static RoomActivationRules roomDetailRequired() {
        return context -> result(context != null && context.getRoomDetail() != null,
                "Room details must not be null");
    }

    private static ValidationResult result(boolean valid, String message) {
        return new ValidationResult(valid ? List.of() : List.of(message));
    }
}
