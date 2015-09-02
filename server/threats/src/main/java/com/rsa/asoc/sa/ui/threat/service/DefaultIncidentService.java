package com.rsa.asoc.sa.ui.threat.service;

import com.rsa.asoc.sa.ui.common.data.Request;
import com.rsa.asoc.sa.ui.common.endpoint.EndpointService;
import com.rsa.asoc.sa.ui.common.endpoint.domain.bean.Endpoint;
import com.rsa.asoc.sa.ui.common.endpoint.domain.bean.EndpointType;
import com.rsa.asoc.sa.ui.threat.domain.bean.Incident;
import com.rsa.asoc.sa.ui.threat.domain.repository.IncidentRepository;
import com.rsa.netwitness.carlos.transport.MessageEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Default implementation of {@link IncidentService} that provides a default {@link MessageEndpoint} for the
 * Incident Management service.
 *
 * @author Abram Thielke
 * @since 10.6.0.0
 */
@Service
public class DefaultIncidentService implements IncidentService {

    private final IncidentRepository incidentRepository;
    private final EndpointService endpointService;

    @Autowired
    public DefaultIncidentService(IncidentRepository incidentRepository, EndpointService endpointService) {
        this.incidentRepository = incidentRepository;
        this.endpointService = endpointService;
    }

    @Override
    public CompletableFuture<Optional<Incident>> findIncidentById(String id) {
        return getDefaultIncidentManagementEndpoint()
                .thenCompose((endpoint) -> incidentRepository.findById(endpoint.get(), id));
    }

    @Override
    public CompletableFuture<List<Incident>> findIncidents(Request request) {
        return getDefaultIncidentManagementEndpoint()
                .thenCompose((endpoint) -> incidentRepository.find(endpoint.get(), request));
    }

    @Override
    public CompletableFuture<Long> countIncidents(Request request) {
        return getDefaultIncidentManagementEndpoint()
                .thenCompose((endpoint) -> incidentRepository.count(endpoint.get(), request));
    }

    private CompletableFuture<Optional<Endpoint>> getDefaultIncidentManagementEndpoint() {
        return endpointService.getEndpointsByType(EndpointType.INCIDENT_MANAGEMENT).thenApply((endpoints) ->
                endpoints.stream().findFirst());
    }
}
