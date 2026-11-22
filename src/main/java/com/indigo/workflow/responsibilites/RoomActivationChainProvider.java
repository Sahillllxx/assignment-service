package com.indigo.workflow.responsibilites;

import com.indigo.design.cor.Responsibility;
import com.indigo.design.cor.ResponsibilityChain;
import com.indigo.design.cor.responsibilities.cache.CacheResponsibility;
import com.indigo.design.cor.responsibilities.events.producer.EventProducerResponsibility;
import com.indigo.design.cor.responsibilities.persistence.command.PersistenceResponsibility;
import com.indigo.design.cor.responsibilities.persistence.query.PersistenceQueryResponsibility;
import com.indigo.design.cor.responsibilities.processing.ProcessingResponsibility;
import com.indigo.design.cor.responsibilities.search.SearchClient;
import com.indigo.design.cor.responsibilities.search.SearchEngineResponsibility;
import com.indigo.design.cor.responsibilities.validation.ValidatorResponsibility;
import com.indigo.mapper.AssignmentEventMapper;
import com.indigo.mapper.DeskAssignmentMapper;
import com.indigo.model.context.RoomActivationContext;
import com.indigo.model.event.FlightRoomDetails;
import com.indigo.repository.FlightAssignmentRepository;
import com.indigo.repository.ReactivationCommand;
import com.indigo.service.AssignmentCacheService;
import com.indigo.service.DeskAssignmentDiffService;
import com.indigo.service.FlightAssignmentPublisher;
import com.indigo.service.ReactivationProcessor;
import com.indigo.workflow.validations.RoomActivationValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Optional;

@Configuration(proxyBeanMethods = false)
class RoomActivationChainProvider {

    @Bean
    ResponsibilityChain<RoomActivationContext> roomActivatedChain(RoomActivationValidator validator,
                                                                  SearchClient<FlightRoomDetails, List<String>> searchClient,
                                                                  DeskAssignmentMapper deskAssignmentMapper,
                                                                  FlightAssignmentRepository assignmentRepository,
                                                                  ReactivationProcessor reactivationProcessor,
                                                                  ReactivationCommand reactivationCommand,
                                                                  DeskAssignmentDiffService deskAssignmentDiffService,
                                                                  AssignmentCacheService assignmentCacheService,
                                                                  AssignmentEventMapper assignmentEventMapper,
                                                                  FlightAssignmentPublisher assignmentPublisher) {
        return new ResponsibilityChain<>(List.of(
                handleValidation(validator),
                handleQuery(assignmentRepository),
                handleReactivationProcessing(reactivationProcessor),
                handleReactivationPersistence(reactivationCommand),
                handleSearch(searchClient, deskAssignmentMapper),
                handleDiffProcessing(deskAssignmentDiffService),
                handleSave(assignmentRepository),
                handleCache(assignmentCacheService),
                handlePublish(assignmentPublisher, assignmentEventMapper)
        ));
    }

    private Responsibility<RoomActivationContext> handleValidation(RoomActivationValidator validator) {
        return new ValidatorResponsibility<>(validator);
    }

    private Responsibility<RoomActivationContext> handleQuery(FlightAssignmentRepository repository) {
        return new PersistenceQueryResponsibility<>(
                repository::findByFlightId,
                RoomActivationContext::requireFlightId,
                (context, assignment) -> context.setExistingAssignment(assignment.orElse(null)));
    }

    private Responsibility<RoomActivationContext> handleReactivationProcessing(ReactivationProcessor processor) {
        return new ProcessingResponsibility<>(processor::process);
    }

    private Responsibility<RoomActivationContext> handleReactivationPersistence(ReactivationCommand command) {
        return new PersistenceResponsibility<>(
                command,
                context -> Optional.ofNullable(context.getReactivationAssignmentId()),
                (context, assignment) -> {
                    context.setExistingAssignment(assignment);
                    context.setWasReactivated(true);
                    context.setReactivationAssignmentId(null);
                });
    }

    private Responsibility<RoomActivationContext> handleSearch(
            SearchClient<FlightRoomDetails, List<String>> searchClient, DeskAssignmentMapper mapper) {
        return new SearchEngineResponsibility<>(
                searchClient, RoomActivationContext::getRoomDetail, mapper::appendAssignments);
    }

    private Responsibility<RoomActivationContext> handleDiffProcessing(DeskAssignmentDiffService service) {
        return new ProcessingResponsibility<>(service::process);
    }

    private Responsibility<RoomActivationContext> handleSave(FlightAssignmentRepository repository) {
        return new PersistenceResponsibility<>(
                repository::save,
                context -> Optional.of(context.getDeskAssignmentDiff().getAssignment()),
                RoomActivationContext::setSavedAssignment);
    }

    private Responsibility<RoomActivationContext> handleCache(AssignmentCacheService service) {
        return new CacheResponsibility<>(
                service,
                context -> context.getSavedAssignment().getFlightId(),
                context -> context,
                RoomActivationContext::setCacheSynced);
    }

    private Responsibility<RoomActivationContext> handlePublish(
            FlightAssignmentPublisher publisher, AssignmentEventMapper mapper) {
        return new EventProducerResponsibility<>(
                publisher, mapper::map, RoomActivationContext::setEventPublishSubmitted);
    }
}
