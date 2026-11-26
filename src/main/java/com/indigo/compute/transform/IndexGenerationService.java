package com.indigo.compute.transform;

import com.indigo.compute.extract.model.AssignmentSourceData;
import com.indigo.compute.index.model.AssignmentIndexSnapshot;

public interface IndexGenerationService {
    AssignmentIndexSnapshot generate(AssignmentSourceData data);
}