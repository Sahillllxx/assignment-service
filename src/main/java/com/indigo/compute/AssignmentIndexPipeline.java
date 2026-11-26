package com.indigo.compute;

import java.util.Collection;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import com.indigo.compute.extract.SourceReader;
import com.indigo.compute.extract.model.AircraftMaster;
import com.indigo.compute.extract.model.DeskAssignmentConfig;
import com.indigo.compute.extract.model.AssignmentSourceData;
import com.indigo.compute.index.model.AssignmentIndexSnapshot;
import com.indigo.compute.load.AssignmentIndexWriter;
import com.indigo.compute.transform.IndexGenerationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssignmentIndexPipeline {
    private final SourceReader<DeskAssignmentConfig> configurationReader;
    private final SourceReader<AircraftMaster> aircraftMasterReader;
    private final IndexGenerationService indexGenerationService;
    private final AssignmentIndexWriter indexWriter;

    public void orchestrate() {
        log.info("Starting desk assignment index rebuild");
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Collection<DeskAssignmentConfig> configs = configurationReader.read();
        Collection<AircraftMaster> masters = aircraftMasterReader.read();
        AssignmentSourceData data = AssignmentSourceData.builder()
            .configs(configs)
            .aircraftMasters(masters)
            .build();
        AssignmentIndexSnapshot snapshot = indexGenerationService.generate(data);
        indexWriter.save(snapshot);
        stopWatch.stop();
        log.info("Desk assignment indexes rebuilt in {} ms (configs={}, aircraftMasters={})",
            stopWatch.getTotalTimeMillis(), configs.size(), masters.size());
    }
}