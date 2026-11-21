package com.indigo.design;


import com.indigo.design.cor.ChainDecision;

import java.util.Objects;

public record ProcessingResult<S>(S state, ChainDecision decision) {

    public ProcessingResult {
        Objects.requireNonNull(state, "Processing result must contain a state");
        if (decision == null) {
            throw new IllegalArgumentException("Processing result must contain a decision");
        }
    }

}
