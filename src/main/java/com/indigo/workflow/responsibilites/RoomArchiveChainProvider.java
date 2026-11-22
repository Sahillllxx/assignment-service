package com.indigo.workflow.responsibilites;

import com.indigo.design.cor.Responsibility;
import com.indigo.design.cor.ResponsibilityChain;
import com.indigo.design.cor.responsibilities.cache.CacheResponsibility;
import com.indigo.design.cor.responsibilities.events.producer.EventProducerResponsibility;
import com.indigo.design.cor.responsibilities.persistence.command.PersistenceResponsibility;
import com.indigo.design.cor.responsibilities.persistence.query.PersistenceQueryResponsibility;
import com.indigo.design.cor.responsibilities.processing.ProcessingResponsibility;
import com.indigo.design.cor.responsibilities.validation.ValidatorResponsibility;
import com.indigo.mapper.ArchiveEventMapper;
import com.indigo.model.context.RoomArchiveContext;
import com.indigo.repository.FlightAssignmentRepository;
import com.indigo.service.ArchiveAssignmentCacheClient;
import com.indigo.service.ArchiveProcessor;
import com.indigo.service.FlightAssignmentPublisher;
import com.indigo.workflow.validations.RoomArchiveValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Optional;

@Configuration(proxyBeanMethods = false)
class RoomArchiveChainProvider {

    @Bean
    ResponsibilityChain<RoomArchiveContext> roomArchivedChain(RoomArchiveValidator validator,
                                                              FlightAssignmentRepository assignmentRepository,
                                                              ArchiveProcessor archiveProcessor,
                                                              ArchiveAssignmentCacheClient archiveCacheClient,
                                                              ArchiveEventMapper eventMapper,
                                                              FlightAssignmentPublisher publisher) {
        return new ResponsibilityChain<>(List.of(
                handleValidation(validator),
                handleQuery(assignmentRepository),
                handleArchiveProcessing(archiveProcessor),
                handlePersistence(assignmentRepository),
                handleCaching(archiveCacheClient),
                handlePublishing(publisher, eventMapper)
        ));
    }

    private Responsibility<RoomArchiveContext> handleValidation(RoomArchiveValidator validator) {
        return new ValidatorResponsibility<>(validator);
    }

    private Responsibility<RoomArchiveContext> handleQuery(FlightAssignmentRepository repository) {
        return new PersistenceQueryResponsibility<>(
                repository::findByFlightId,
                RoomArchiveContext::getId,
                (context, assignment) -> context.setExistingAssignment(assignment.orElse(null)));
    }

    private Responsibility<RoomArchiveContext> handleArchiveProcessing(ArchiveProcessor archiveProcessor) {
        return new ProcessingResponsibility<>(archiveProcessor::process);
    }

    private Responsibility<RoomArchiveContext> handlePersistence(FlightAssignmentRepository repository) {
        return new PersistenceResponsibility<>(
                repository::save,
                context -> Optional.of(context.getExistingAssignment()),
                RoomArchiveContext::setSavedAssignment);
    }

    private Responsibility<RoomArchiveContext> handleCaching(ArchiveAssignmentCacheClient cacheClient) {
        return new CacheResponsibility<>(
                cacheClient,
                context -> context.getSavedAssignment().getFlightId(),
                context -> context,
                RoomArchiveContext::setCacheSynced);
    }

    private Responsibility<RoomArchiveContext> handlePublishing(FlightAssignmentPublisher publisher, ArchiveEventMapper archiveEventMapper) {
        return new EventProducerResponsibility<>(
                publisher, archiveEventMapper::map, RoomArchiveContext::setEventPublishSubmitted);
    }
}
