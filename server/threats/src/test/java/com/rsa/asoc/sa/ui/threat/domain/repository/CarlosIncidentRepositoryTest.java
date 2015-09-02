package com.rsa.asoc.sa.ui.threat.domain.repository;

import com.google.common.collect.ImmutableMap;
import com.rsa.asoc.sa.ui.common.endpoint.domain.bean.Endpoint;
import com.rsa.asoc.sa.ui.common.protobuf.ProtocolBufferUtils;
import com.rsa.asoc.sa.ui.common.data.Request;
import com.rsa.asoc.sa.ui.threat.domain.bean.Incident;
import com.rsa.netwitness.carlos.plist.PropertyListProtocol;
import com.rsa.netwitness.carlos.transport.MessageChannel;
import com.rsa.netwitness.carlos.transport.MessageChannelListener;
import com.rsa.netwitness.im.IMProtocol;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.convert.ConversionService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link CarlosIncidentRepository}
 *
 * @author Abram Thielke
 * @since 10.6.0.0
 */
public class CarlosIncidentRepositoryTest {

    private final Map<String, Object> incidentMap = ImmutableMap.<String, Object>builder()
            .put("id", "INC-1")
            .put("name", "Testing Incident")
            .build();

    private final PropertyListProtocol.Dictionary incidentDictionary =
            ProtocolBufferUtils.createDictionaryFromMap(incidentMap);

    @Mock
    private ConversionService conversionService;

    @Mock
    private Endpoint endpoint;

    @Mock
    private MessageChannel<IMProtocol.IncidentMessage> messageChannel;

    @Captor
    private ArgumentCaptor<MessageChannelListener<IMProtocol.IncidentMessage>> captor;

    private IncidentRepository incidentRepository;

    @Before
    public void init() throws Exception {
        Incident incident = new Incident();
        incident.setId("INC-1");
        incident.setName("Testing Incident");

        MockitoAnnotations.initMocks(this);
        when(endpoint.getMessageChannel(eq(IMProtocol.IncidentMessage.class))).thenReturn(messageChannel);
        when(conversionService.convert(any(), eq(Incident.class))).thenReturn(incident);

        incidentRepository = new CarlosIncidentRepository(conversionService);
    }

    @Test
    public void testFindById() throws Exception {
        String id = "INC-1";
        CompletableFuture<Optional<Incident>> future = incidentRepository.findById(endpoint, id);
        assertNotNull(future);

        verify(messageChannel).sendRequest(any(IMProtocol.IncidentMessage.class), captor.capture());
        IMProtocol.IncidentMessage message = IMProtocol.IncidentMessage.newBuilder()
                .setType(IMProtocol.IncidentMessage.IncidentMessageType.GetIncidentResponse)
                .setGetIncidentResponse(IMProtocol.GetIncidentResponse.newBuilder()
                        .setIncident(incidentDictionary))
                .build();
        captor.getValue().onMessage(null, null, message);

        verify(conversionService).convert(any(), eq(Incident.class));

        assertTrue(future.isDone());
        Optional<Incident> optional = future.get();
        assertTrue(optional.isPresent());
        Incident incident = optional.get();
        assertEquals(id, incident.getId());
    }

    @Test
    public void testFindByIdWithUnknownId() throws Exception {
        String id = "DOES-NOT-EXIST";
        CompletableFuture<Optional<Incident>> future = incidentRepository.findById(endpoint, id);
        assertNotNull(future);

        verify(messageChannel).sendRequest(any(IMProtocol.IncidentMessage.class), captor.capture());
        captor.getValue().onException(new NullPointerException("IM throws an NPE when an ID is not found."));

        assertTrue(future.isDone());
        Optional<Incident> optional = future.get();
        assertFalse(optional.isPresent());
    }

    @Test
    public void testFindByIdWithException() throws Exception {
        String id = "INC-1";
        CompletableFuture<Optional<Incident>> future = incidentRepository.findById(endpoint, id);
        assertNotNull(future);

        verify(messageChannel).sendRequest(any(IMProtocol.IncidentMessage.class), captor.capture());
        captor.getValue().onException(new RuntimeException("Some random exception"));

        assertTrue(future.isDone());
        assertTrue(future.isCompletedExceptionally());
    }

    @Test
    public void testFind() throws Exception {
        Request request = Request.newBuilder()
                .withPage(Request.Page.newBuilder()
                        .withIndex(0)
                        .withSize(10))
                .withFilter(Request.Filter.newBuilder()
                        .withField("status")
                        .withValue("NEW"))
                .withSort(Request.Sort.newBuilder()
                        .withField("created")
                        .withDescending(true))
                .build();

        CompletableFuture<List<Incident>> future = incidentRepository.find(endpoint, request);
        assertNotNull(future);

        verify(messageChannel).sendRequest(any(IMProtocol.IncidentMessage.class), captor.capture());
        IMProtocol.IncidentMessage message = IMProtocol.IncidentMessage.newBuilder()
                .setType(IMProtocol.IncidentMessage.IncidentMessageType.FindIncidentsResponse)
                .setFindIncidentsResponse(IMProtocol.FindIncidentsResponse.newBuilder()
                        .addIncident(incidentDictionary)
                        .addIncident(incidentDictionary)
                        .addIncident(incidentDictionary))
                .build();
        captor.getValue().onMessage(null, null, message);

        verify(conversionService, times(3)).convert(any(), eq(Incident.class));

        assertTrue(future.isDone());
        List<Incident> incidents = future.get();
        assertNotNull(incidents);
        assertEquals(3, incidents.size());
    }

    @Test
    public void testFindWithNoResults() throws Exception {
        Request request = Request.newBuilder()
                .withPage(Request.Page.newBuilder()
                        .withIndex(0)
                        .withSize(10))
                .withFilter(Request.Filter.newBuilder()
                        .withField("status")
                        .withValue("NEW"))
                .withSort(Request.Sort.newBuilder()
                        .withField("created")
                        .withDescending(true))
                .build();

        CompletableFuture<List<Incident>> future = incidentRepository.find(endpoint, request);
        assertNotNull(future);

        verify(messageChannel).sendRequest(any(IMProtocol.IncidentMessage.class), captor.capture());
        IMProtocol.IncidentMessage message = IMProtocol.IncidentMessage.newBuilder()
                .setType(IMProtocol.IncidentMessage.IncidentMessageType.FindIncidentsResponse)
                .setFindIncidentsResponse(IMProtocol.FindIncidentsResponse.getDefaultInstance())
                .build();
        captor.getValue().onMessage(null, null, message);

        verify(conversionService, never()).convert(any(), eq(Incident.class));

        assertTrue(future.isDone());
        List<Incident> incidents = future.get();
        assertNotNull(incidents);
        assertTrue(incidents.isEmpty());
    }

    @Test
    public void testFindWithRemoteException() throws Exception {
        Request request = Request.newBuilder()
                .withPage(Request.Page.newBuilder()
                        .withIndex(0)
                        .withSize(10))
                .withFilter(Request.Filter.newBuilder()
                        .withField("status")
                        .withValue("NEW"))
                .withSort(Request.Sort.newBuilder()
                        .withField("created")
                        .withDescending(true))
                .build();

        CompletableFuture<List<Incident>> future = incidentRepository.find(endpoint, request);
        assertNotNull(future);

        verify(messageChannel).sendRequest(any(IMProtocol.IncidentMessage.class), captor.capture());
        captor.getValue().onException(new RuntimeException("Failed on the remote end"));

        verify(conversionService, never()).convert(any(), eq(Incident.class));

        assertTrue(future.isDone());
        assertTrue(future.isCompletedExceptionally());
    }

    @Test
    public void testCount() throws Exception {
        Request request = Request.newBuilder()
                .withPage(Request.Page.newBuilder()
                        .withIndex(0)
                        .withSize(10))
                .withFilter(Request.Filter.newBuilder()
                        .withField("status")
                        .withValue("NEW"))
                .withSort(Request.Sort.newBuilder()
                        .withField("created")
                        .withDescending(true))
                .build();

        CompletableFuture<Long> future = incidentRepository.count(endpoint, request);
        assertNotNull(future);

        verify(messageChannel).sendRequest(any(IMProtocol.IncidentMessage.class), captor.capture());
        Map<String, Object> response = new HashMap<>();
        response.put("NOT_SET", 1789);

        IMProtocol.IncidentMessage message = IMProtocol.IncidentMessage.newBuilder()
                .setType(IMProtocol.IncidentMessage.IncidentMessageType.GetIncidentCountByGroupResponse)
                .setGetIncidentCountByGroupResponse(IMProtocol.GetIncidentCountByGroupResponse.newBuilder()
                        .setGroupedIncidentCount(ProtocolBufferUtils.createDictionaryFromMap(response)))
                .build();
        captor.getValue().onMessage(null, null, message);

        assertTrue(future.isDone());
        Long count = future.get();
        assertEquals(Long.valueOf(1789), count);
    }

    @Test
    public void testCountWithNoResults() throws Exception {
        Request request = Request.newBuilder()
                .withPage(Request.Page.newBuilder()
                        .withIndex(0)
                        .withSize(10))
                .withFilter(Request.Filter.newBuilder()
                        .withField("status")
                        .withValue("NEW"))
                .withSort(Request.Sort.newBuilder()
                        .withField("created")
                        .withDescending(true))
                .build();

        CompletableFuture<Long> future = incidentRepository.count(endpoint, request);
        assertNotNull(future);

        verify(messageChannel).sendRequest(any(IMProtocol.IncidentMessage.class), captor.capture());
        IMProtocol.IncidentMessage message = IMProtocol.IncidentMessage.newBuilder()
                .setType(IMProtocol.IncidentMessage.IncidentMessageType.GetIncidentCountByGroupResponse)
                .setGetIncidentCountByGroupResponse(IMProtocol.GetIncidentCountByGroupResponse.newBuilder()
                        .setGroupedIncidentCount(ProtocolBufferUtils.createDictionaryFromMap(new HashMap<>())))
                .build();
        captor.getValue().onMessage(null, null, message);

        assertTrue(future.isDone());
        Long count = future.get();
        assertEquals(Long.valueOf(0), count);
    }

    @Test
    public void testCountWithRemoteException() throws Exception {
        Request request = Request.newBuilder()
                .withPage(Request.Page.newBuilder()
                        .withIndex(0)
                        .withSize(10))
                .withFilter(Request.Filter.newBuilder()
                        .withField("status")
                        .withValue("NEW"))
                .withSort(Request.Sort.newBuilder()
                        .withField("created")
                        .withDescending(true))
                .build();

        CompletableFuture<Long> future = incidentRepository.count(endpoint, request);
        assertNotNull(future);

        verify(messageChannel).sendRequest(any(IMProtocol.IncidentMessage.class), captor.capture());
        captor.getValue().onException(new RuntimeException("Failed on the remote end"));

        assertTrue(future.isDone());
        assertTrue(future.isCompletedExceptionally());
    }
}
