package com.indigo.compute.extract.mongo;

import java.util.Collection;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import com.indigo.compute.extract.SourceReader;
import com.indigo.compute.extract.model.DeskAssignmentConfig;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MongoDeskAssignmentConfigReader implements SourceReader<DeskAssignmentConfig> {
    private static final String IS_ACTIVE = "isActive";
    private final MongoTemplate mongoTemplate;

    @Override
    public Collection<DeskAssignmentConfig> read() {
        Query query = Query.query(Criteria.where(IS_ACTIVE).is(true));
        return mongoTemplate.find(query, DeskAssignmentConfig.class);
    }
}