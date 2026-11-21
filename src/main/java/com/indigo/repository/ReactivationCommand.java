package com.indigo.repository;

import com.indigo.design.cor.responsibilities.persistence.command.PersistenceCommandClient;
import com.indigo.model.FlightAssignment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public final class ReactivationCommand implements PersistenceCommandClient<String, FlightAssignment> {
    private final MongoTemplate mongoTemplate;

    @Override
    public FlightAssignment execute(String assignmentId) {
        Query query = Query.query(Criteria.where("id").is(assignmentId).and("isActive").is(false));
        Update update = new Update().set("isActive", true).set("updatedAt", Instant.now());
        FlightAssignment reactivated = mongoTemplate.findAndModify(
                query, update, FindAndModifyOptions.options().returnNew(true), FlightAssignment.class);
        if (reactivated == null) {
            throw new IllegalStateException("Flight assignment changed or disappeared during reactivation");
        }
        log.info("Reactivated flight assignment for flightId={}", reactivated.getFlightId());
        return reactivated;
    }
}
