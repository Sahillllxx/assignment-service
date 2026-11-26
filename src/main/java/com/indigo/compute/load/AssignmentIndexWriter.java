package com.indigo.compute.load;

import com.indigo.compute.index.model.AssignmentIndexSnapshot;

public interface AssignmentIndexWriter {
    void save(AssignmentIndexSnapshot snapshot);
}