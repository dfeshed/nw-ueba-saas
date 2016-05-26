package com.rsa.asoc.sa.ui.threat.web.socket;

import com.rsa.asoc.sa.ui.common.data.Request;
import com.rsa.asoc.sa.ui.common.data.Response;
import com.rsa.asoc.sa.ui.common.data.ResponseCode;
import com.rsa.asoc.sa.ui.common.service.WebSocketSubscriptionService;
import com.rsa.asoc.sa.ui.test.stomp.StompMessageUtils;
import com.rsa.asoc.sa.ui.test.stomp.TestingMessageChannel;
import com.rsa.asoc.sa.ui.test.stomp.TestingStompMessageHandler;
import com.rsa.asoc.sa.ui.threat.domain.bean.Incident;
import com.rsa.asoc.sa.ui.threat.service.IncidentService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link IncidentController}
 *
 * @author Abram Thielke
 * @since 10.6.0.0
 */
public class IncidentControllerTest {

    private final ParameterizedTypeReference<Response<List<Incident>>> responseType =
            new ParameterizedTypeReference<Response<List<Incident>>>() {};

    private final AtomicInteger id = new AtomicInteger(0);

    private TestingMessageChannel outboundMessageChannel;
    private TestingStompMessageHandler testingStompMessageHandler;

    @Mock
    private IncidentService incidentService;

    @Mock
    private WebSocketSubscriptionService webSocketSubscriptionService;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        outboundMessageChannel = new TestingMessageChannel();

        IncidentController incidentController =
                new IncidentController(new IncidentControllerSettings(), incidentService, webSocketSubscriptionService,
                        new SimpMessagingTemplate(outboundMessageChannel));

        testingStompMessageHandler = new TestingStompMessageHandler(outboundMessageChannel, incidentController);
    }

    @Test
    public void testFindIncidents() {
        List<Incident> incidents = createIncidents(5);

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

        assertEquals(1, outboundMessageChannel.getMessages().size());
        Message<Response<List<Incident>>> reply =
                outboundMessageChannel.popMessage(responseType);

        StompHeaderAccessor replyHeaders = StompHeaderAccessor.wrap(reply);
        assertEquals("/user/admin/queue/threats/incidents", replyHeaders.getDestination());

        Response<List<Incident>> response = reply.getPayload();
        assertEquals(ResponseCode.SUCCESS, response.getCode());
        assertNotNull(response.getRequest());
        assertEquals(5, response.getData().size());
        assertEquals(10L, response.getMeta().get("total"));
    }

    @Test
    public void testFindIncidentsWithError() {
        when(incidentService.findIncidents(any(Request.class))).thenReturn(
                CompletableFuture.completedFuture(new ArrayList<>()));
        when(incidentService.countIncidents(any(Request.class))).thenThrow( new RuntimeException("Error"));

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

        assertEquals(1, outboundMessageChannel.getMessages().size());

        Message<Response<List<Incident>>> reply = outboundMessageChannel.popMessage(responseType);

        StompHeaderAccessor replyHeaders = StompHeaderAccessor.wrap(reply);
        assertEquals("/user/admin/queue/threats/incidents", replyHeaders.getDestination());

        Response<List<Incident>> response = reply.getPayload();
        assertEquals(ResponseCode.GENERAL_EXCEPTION, response.getCode());
        assertNotNull(response.getRequest());
        assertNull(response.getData());
    }

    @Test
    public void testStreamIncidents() throws Exception {
        outboundMessageChannel.setCountDownLatch(new CountDownLatch(3));

        List<Incident> incidents = createIncidents(100);

        when(incidentService.findIncidents(any(Request.class)))
                .thenReturn(CompletableFuture.completedFuture(incidents.subList(0, 25)))
                .thenReturn(CompletableFuture.completedFuture(incidents.subList(25, 50)))
                .thenReturn(CompletableFuture.completedFuture(incidents.subList(50, 60)));
        when(incidentService.countIncidents(any(Request.class)))
                .thenReturn(CompletableFuture.completedFuture(100L));

        Request request = Request.newBuilder()
                .withStream(Request.Stream.newBuilder()
                        .withLimit(60L))
                .build();

        Message<?> message =
                StompMessageUtils.createMessage(StompCommand.SEND, "/ws/threats/incidents/stream", "admin", request);

        testingStompMessageHandler.handleMessage(message);
        boolean finished = outboundMessageChannel.await();
        assertTrue(finished);

        verify(incidentService, times(3)).findIncidents(any(Request.class));
        verify(incidentService, times(1)).countIncidents(any(Request.class));

        assertEquals(3, outboundMessageChannel.getMessages().size());

        Message<Response<List<Incident>>> outbound = outboundMessageChannel.popMessage(responseType);
        StompHeaderAccessor replyHeaders = StompHeaderAccessor.wrap(outbound);
        assertEquals("/user/admin/queue/threats/incidents", replyHeaders.getDestination());

        Response<List<Incident>> response = outbound.getPayload();
        assertEquals(ResponseCode.SUCCESS, response.getCode());
        assertNotNull(response.getRequest());
        assertEquals(25, response.getData().size());
        assertEquals(100L, response.getMeta().get("total"));
        assertNull(response.getRequest().getPage());

        outbound = outboundMessageChannel.popMessage(responseType);
        replyHeaders = StompHeaderAccessor.wrap(outbound);
        assertEquals("/user/admin/queue/threats/incidents", replyHeaders.getDestination());

        response = outbound.getPayload();
        assertEquals(ResponseCode.SUCCESS, response.getCode());
        assertNotNull(response.getRequest());
        assertEquals(25, response.getData().size());
        assertEquals(100L, response.getMeta().get("total"));
        assertNull(response.getRequest().getPage());

        outbound = outboundMessageChannel.popMessage(responseType);
        replyHeaders = StompHeaderAccessor.wrap(outbound);
        assertEquals("/user/admin/queue/threats/incidents", replyHeaders.getDestination());

        response = outbound.getPayload();
        assertEquals(ResponseCode.SUCCESS, response.getCode());
        assertNotNull(response.getRequest());
        assertEquals(10, response.getData().size());
        assertEquals(100L, response.getMeta().get("total"));
        assertNull(response.getRequest().getPage());
    }

    @Test
    public void testStreamIncidentsWithLessThanStreamLimit() throws Exception {
        outboundMessageChannel.setCountDownLatch(new CountDownLatch(2));

        List<Incident> incidents = createIncidents(30);

        when(incidentService.findIncidents(any(Request.class)))
                .thenReturn(CompletableFuture.completedFuture(incidents.subList(0, 25)))
                .thenReturn(CompletableFuture.completedFuture(incidents.subList(25, 30)));
        when(incidentService.countIncidents(any(Request.class)))
                .thenReturn(CompletableFuture.completedFuture(30L));

        Request request = Request.newBuilder()
                .withStream(Request.Stream.newBuilder()
                        .withLimit(1000L))
                .build();

        Message<?> message =
                StompMessageUtils.createMessage(StompCommand.SEND, "/ws/threats/incidents/stream", "admin", request);

        testingStompMessageHandler.handleMessage(message);
        boolean finished = outboundMessageChannel.await();
        assertTrue(finished);

        verify(incidentService, times(2)).findIncidents(any(Request.class));
        verify(incidentService, times(1)).countIncidents(any(Request.class));

        assertEquals(2, outboundMessageChannel.getMessages().size());

        Message<Response<List<Incident>>> outbound = outboundMessageChannel.popMessage(responseType);
        StompHeaderAccessor replyHeaders = StompHeaderAccessor.wrap(outbound);
        assertEquals("/user/admin/queue/threats/incidents", replyHeaders.getDestination());

        Response<List<Incident>> response = outbound.getPayload();
        assertEquals(ResponseCode.SUCCESS, response.getCode());
        assertNotNull(response.getRequest());
        assertEquals(25, response.getData().size());
        assertEquals(30L, response.getMeta().get("total"));
        assertNull(response.getRequest().getPage());

        outbound = outboundMessageChannel.popMessage(responseType);
        replyHeaders = StompHeaderAccessor.wrap(outbound);
        assertEquals("/user/admin/queue/threats/incidents", replyHeaders.getDestination());

        response = outbound.getPayload();
        assertEquals(ResponseCode.SUCCESS, response.getCode());
        assertNotNull(response.getRequest());
        assertEquals(5, response.getData().size());
        assertEquals(30L, response.getMeta().get("total"));
        assertNull(response.getRequest().getPage());
    }

    @Test
    public void testStreamIncidentsWithInvalidStreamLimit() throws Exception {
        outboundMessageChannel.setCountDownLatch(new CountDownLatch(1));

        Request request = Request.newBuilder()
                .withStream(Request.Stream.newBuilder()
                        .withLimit(0L))
                .build();

        Message<?> message =
                StompMessageUtils.createMessage(StompCommand.SEND, "/ws/threats/incidents/stream", "admin", request);

        testingStompMessageHandler.handleMessage(message);
        boolean finished = outboundMessageChannel.await();
        assertTrue(finished);

        assertEquals(1, outboundMessageChannel.getMessages().size());

        Message<Response<List<Incident>>> outbound = outboundMessageChannel.popMessage(responseType);
        StompHeaderAccessor replyHeaders = StompHeaderAccessor.wrap(outbound);
        assertEquals("/user/admin/queue/threats/incidents", replyHeaders.getDestination());

        Response<?> response = outbound.getPayload();
        assertEquals(ResponseCode.GENERAL_EXCEPTION, response.getCode());
        assertNotNull(response.getRequest());
        assertNull(response.getData());
        assertNotNull(response.getMeta().get("message"));
    }

    @Test
    public void testStreamIncidentsWithMissingStreamLimit() throws Exception {
        outboundMessageChannel.setCountDownLatch(new CountDownLatch(1));

        Request request = Request.newBuilder().build();

        Message<?> message =
                StompMessageUtils.createMessage(StompCommand.SEND, "/ws/threats/incidents/stream", "admin", request);

        testingStompMessageHandler.handleMessage(message);
        boolean finished = outboundMessageChannel.await();
        assertTrue(finished);

        assertEquals(1, outboundMessageChannel.getMessages().size());

        Message<Response<List<Incident>>> outbound = outboundMessageChannel.popMessage(responseType);
        StompHeaderAccessor replyHeaders = StompHeaderAccessor.wrap(outbound);
        assertEquals("/user/admin/queue/threats/incidents", replyHeaders.getDestination());

        Response<?> response = outbound.getPayload();
        assertEquals(ResponseCode.GENERAL_EXCEPTION, response.getCode());
        assertNotNull(response.getRequest());
        assertNull(response.getData());
        assertNotNull(response.getMeta().get("message"));
    }

    private List<Incident> createIncidents(int count) {
        return Stream.iterate(0, n -> n + 1)
                .map((counter) -> createIncident())
                .limit(count)
                .collect(Collectors.toList());
    }

    private Incident createIncident() {
        Incident incident = new Incident();
        incident.setId("INC-" + id.incrementAndGet());
        incident.setName("Testing Incident");
        return incident;
    }
}
