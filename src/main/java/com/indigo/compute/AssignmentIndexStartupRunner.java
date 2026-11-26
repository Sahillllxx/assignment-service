package com.indigo.compute;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

/**
 * Rebuilds the Redis indexes from Mongo once, after all singletons are initialised but before the
 * web server starts accepting requests.
 */
@Component
@RequiredArgsConstructor
public class AssignmentIndexStartupRunner implements SmartInitializingSingleton {
    private final AssignmentIndexPipeline pipeline;

    @Override
    public void afterSingletonsInstantiated() {
        pipeline.orchestrate();
    }
}
