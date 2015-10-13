package com.rsa.asoc.sa.ui.threat.web.socket;

import com.rsa.asoc.sa.ui.common.data.ResponseCode;
import com.rsa.asoc.sa.ui.common.data.Response;
import com.rsa.asoc.sa.ui.common.data.Request;
import com.rsa.asoc.sa.ui.common.test.stomp.StompMessageUtils;
import com.rsa.asoc.sa.ui.common.test.stomp.TestingStompMessageHandler;
import com.rsa.asoc.sa.ui.threat.domain.bean.Incident;
import com.rsa.asoc.sa.ui.threat.service.IncidentService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link IncidentController}
 *
 * @author Abram Thielke
 * @since 10.6.0.0
 */
public class IncidentControllerTest {

    private final ParameterizedTypeReference<Response<Collection<Incident>>> responseType =
            new ParameterizedTypeReference<Response<Collection<Incident>>>() {};

    private TestingStompMessageHandler testingStompMessageHandler;
    private IncidentService incidentService;

    @Before
    public void init() {
        incidentService = mock(IncidentService.class);
        IncidentController incidentController = new IncidentController(incidentService);

        testingStompMessageHandler = new TestingStompMessageHandler(incidentController);
    }

    @Test
    public void testFindIncidents() {
        List<Incident> incidents = Arrays.asList(
                createIncident("INC-1"),
                createIncident("INC-2"),
                createIncident("INC-3"),
                createIncident("INC-4"),
                createIncident("INC-5"));
        when(incidentService.findIncidents(any(Request.class))).thenReturn(
                CompletableFuture.completedFuture(incidents));
        when(incidentService.countIncidents(any(Request.class))).thenReturn(
                CompletableFuture.completedFuture(10L));

        Request request = Request.newBuilder()
                .withPage(Request.Page.newBuilder()
                        .withIndex(0)
                        .withSize(5))
                .build();
        Message<?> message =
                StompMessageUtils.createMessage(StompCommand.SEND, "/ws/threats/incidents", "admin", request);

        testingStompMessageHandler.handleMessage(message);

        verify(incidentService).findIncidents(any(Request.class));
        verify(incidentService).countIncidents(any(Request.class));

        assertEquals(1, testingStompMessageHandler.getOutboundMessages().size());
        Message<Response<Collection<Incident>>> reply =
                testingStompMessageHandler.popOutboundMessage(responseType);

        StompHeaderAccessor replyHeaders = StompHeaderAccessor.wrap(reply);
        String destination = StompMessageUtils.getUserDestination("admin", "/queue/incidents");
        assertEquals(destination, replyHeaders.getDestination());

        Response<Collection<Incident>> response = reply.getPayload();
        assertEquals(ResponseCode.SUCCESS, response.getCode());
        assertNotNull(response.getRequest());
        assertEquals(5, response.getData().size());
        assertEquals(10L, response.getMeta().get("total"));
    }

    @Test
    public void testFindIncidentsWithError() {
        when(incidentService.findIncidents(any(Request.class))).thenReturn(
                CompletableFuture.completedFuture(new ArrayList<>()));
        when(incidentService.countIncidents(any(Request.class))).thenThrow(
                new RuntimeException("Error"));

        Request request = Request.newBuilder()
                .withPage(Request.Page.newBuilder()
                        .withIndex(0)
                        .withSize(10))
                .build();
        Message<?> message =
                StompMessageUtils.createMessage(StompCommand.SEND, "/ws/threats/incidents", "admin", request);

        testingStompMessageHandler.handleMessage(message);

        verify(incidentService).findIncidents(any(Request.class));
        verify(incidentService).countIncidents(any(Request.class));

        assertEquals(1, testingStompMessageHandler.getOutboundMessages().size());

        Message<Response<Collection<Incident>>> reply =
                testingStompMessageHandler.popOutboundMessage(responseType);

        StompHeaderAccessor replyHeaders = StompHeaderAccessor.wrap(reply);
        String destination = StompMessageUtils.getUserDestination("admin", "/queue/incidents");
        assertEquals(destination, replyHeaders.getDestination());

        Response<Collection<Incident>> response = reply.getPayload();
        assertEquals(ResponseCode.GENERAL_EXCEPTION, response.getCode());
        assertNotNull(response.getRequest());
        assertNull(response.getData());
    }

    private Incident createIncident(String id) {
        Incident incident = new Incident();
        incident.setId(id);
        incident.setName("Testing Incident");
        return incident;
    }
}
