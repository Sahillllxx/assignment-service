package com.indigo.repository;

import com.indigo.model.FlightAssignment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface FlightAssignmentRepository extends MongoRepository<FlightAssignment, String> {
    Optional<FlightAssignment> findByFlightId(String flightId);
}
