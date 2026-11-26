package com.indigo.compute.transform.builder;

import com.indigo.compute.index.model.AssignmentIndexSnapshot;
import com.indigo.compute.transform.model.IndexBuildContext;

public interface IndexBuilder {
    void buildIndexes(IndexBuildContext context, AssignmentIndexSnapshot target);
}