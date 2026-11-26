package com.indigo.compute.extract.mongo;

import java.util.Collection;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import com.indigo.compute.extract.SourceReader;
import com.indigo.compute.extract.model.AircraftMaster;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MongoAircraftMasterReader implements SourceReader<AircraftMaster> {
    private final MongoTemplate mongoTemplate;

    @Override
    public Collection<AircraftMaster> read() {
        return mongoTemplate.findAll(AircraftMaster.class);
    }
}