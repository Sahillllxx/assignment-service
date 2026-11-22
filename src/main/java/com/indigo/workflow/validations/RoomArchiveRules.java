package com.indigo.workflow.validations;

import com.indigo.design.cor.responsibilities.validation.ValidationResult;
import com.indigo.design.validation.ValidationRule;
import com.indigo.model.context.RoomArchiveContext;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

@FunctionalInterface
public interface RoomArchiveRules extends ValidationRule<RoomArchiveContext> {

    static RoomArchiveRules contextNotNull() {
        return context -> result(Objects.nonNull(context), "Room archive context must not be null");
    }

    static RoomArchiveRules idRequired() {
        return context -> result(Objects.nonNull(context) && StringUtils.hasText(context.getId()),
                "Room archive flight ID must not be blank");
    }

    private static ValidationResult result(boolean valid, String message) {
        return new ValidationResult(valid ? List.of() : List.of(message));
    }
}
