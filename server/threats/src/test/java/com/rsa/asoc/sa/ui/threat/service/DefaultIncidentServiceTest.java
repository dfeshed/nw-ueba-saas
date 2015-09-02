package com.rsa.asoc.sa.ui.threat.service;

import com.rsa.asoc.sa.ui.common.data.Request;
import com.rsa.asoc.sa.ui.common.endpoint.EndpointService;
import com.rsa.asoc.sa.ui.common.endpoint.domain.bean.Endpoint;
import com.rsa.asoc.sa.ui.common.endpoint.domain.bean.EndpointType;
import com.rsa.asoc.sa.ui.common.endpoint.domain.bean.GenericEndpoint;
import com.rsa.asoc.sa.ui.threat.domain.bean.Incident;
import com.rsa.asoc.sa.ui.threat.domain.repository.IncidentRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link DefaultIncidentService}
 *
 * @author Abram Thielke
 * @since 10.6.0.0
 */
public class DefaultIncidentServiceTest {

    @Mock
    private IncidentRepository incidentRepository;

    @Mock
    private EndpointService endpointService;

    @Mock
    private Endpoint endpoint;

    private DefaultIncidentService defaultIncidentService;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        List<Endpoint> endpoints = Collections.singletonList(endpoint);
        when(endpointService.getEndpointsByType(eq(EndpointType.INCIDENT_MANAGEMENT)))
                .thenReturn(CompletableFuture.completedFuture(endpoints));

        defaultIncidentService = new DefaultIncidentService(incidentRepository, endpointService);
    }

    @Test
    public void testFindById() throws Exception {
        Incident incident = createIncident();
        final String id = incident.getId();
        when(incidentRepository.findById(any(Endpoint.class), eq(id)))
                .thenReturn(CompletableFuture.completedFuture(Optional.of(incident)));

        CompletableFuture<Optional<Incident>> future = defaultIncidentService.findIncidentById(id);

        assertTrue(future.isDone());
        Optional<Incident> optional = future.get();

        assertTrue(optional.isPresent());
        Incident response = optional.get();
        assertEquals(id, response.getId());
    }

    @Test
    public void testFindIncidents() throws Exception {
        List<Incident> incidents = Arrays.asList(
                createIncident(),
                createIncident(),
                createIncident(),
                createIncident(),
                createIncident());
        when(incidentRepository.find(any(Endpoint.class), any(Request.class)))
                .thenReturn(CompletableFuture.completedFuture(incidents));

        Request request = new Request();
        CompletableFuture<List<Incident>> future = defaultIncidentService.findIncidents(request);

        assertTrue(future.isDone());
        List<Incident> response = future.get();

        assertEquals(5, response.size());
    }

    @Test
    public void testCountIncidents() throws Exception {
        final long count = 20467;
        when(incidentRepository.count(any(Endpoint.class), any(Request.class)))
                .thenReturn(CompletableFuture.completedFuture(count));

        Request request = new Request();
        CompletableFuture<Long> future = defaultIncidentService.countIncidents(request);

        assertTrue(future.isDone());
        long response = future.get();

        assertEquals(count, response);
    }

    private Incident createIncident() {
        Incident incident = new Incident();
        incident.setId(UUID.randomUUID().toString());
        incident.setName("Testing Incident");
        return incident;
    }

}
