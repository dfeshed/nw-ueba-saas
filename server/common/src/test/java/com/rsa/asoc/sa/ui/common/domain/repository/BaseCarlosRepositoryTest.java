package com.rsa.asoc.sa.ui.common.domain.repository;

import com.rsa.asoc.sa.ui.common.endpoint.domain.bean.Endpoint;
import com.rsa.netwitness.carlos.transport.MessageChannel;
import com.rsa.netwitness.carlos.transport.MessageChannelListener;
import com.rsa.netwitness.carlos.transport.MessageEndpoint;
import com.rsa.netwitness.im.IMProtocol;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link BaseCarlosRepository} that ensure all resources are cleaned up for all outcomes.
 *
 * @author Abram Thielke
 * @since 10.6.0.0
 */
public class BaseCarlosRepositoryTest {

    private final TestRepository repository = new TestRepository();

    @Mock
    private Endpoint endpoint;

    @Mock
    private MessageChannel<IMProtocol.IncidentMessage> channel;

    @Captor
    private ArgumentCaptor<MessageChannelListener<IMProtocol.IncidentMessage>> captor;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSuccessfulResponse() throws Exception {
        when(endpoint.getMessageChannel(eq(IMProtocol.IncidentMessage.class))).thenReturn(channel);

        CompletableFuture<TestPojo> future = repository.findById(endpoint, "INC-1");

        verify(channel).sendRequest(any(IMProtocol.IncidentMessage.class), captor.capture());
        IMProtocol.IncidentMessage message = IMProtocol.IncidentMessage.newBuilder()
                .setType(IMProtocol.IncidentMessage.IncidentMessageType.GetIncidentResponse)
                .build();
        captor.getValue().onMessage(null, null, message);

        assertTrue(future.isDone());
        TestPojo pojo = future.get();
        assertNotNull(pojo);
        assertEquals("INC-1", pojo.id);
        verify(channel).close();
    }

    @Test
    public void testErrorSendingRequest() throws Exception {
        when(endpoint.getMessageChannel(eq(IMProtocol.IncidentMessage.class))).thenReturn(channel);
        doThrow(new Exception("CARLOS Transport Issue"))
                .when(channel).sendRequest(any(IMProtocol.IncidentMessage.class), captor.capture());

        CompletableFuture<TestPojo> future = repository.findById(endpoint, "INC-1");
        assertTrue(future.isCompletedExceptionally());
        verify(channel).close();
    }

    @Test
    public void testErrorOnRemoteEnd() throws Exception {
        when(endpoint.getMessageChannel(eq(IMProtocol.IncidentMessage.class))).thenReturn(channel);

        CompletableFuture<TestPojo> future = repository.findById(endpoint, "INC-1");

        verify(channel).sendRequest(any(IMProtocol.IncidentMessage.class), captor.capture());
        captor.getValue().onException(new NullPointerException("Unexpected Remote Error"));

        assertTrue(future.isCompletedExceptionally());
        verify(channel).close();
    }

    /**
     * A POJO to return from the TestRepository
     */
    private static class TestPojo {
        private String id;

        public TestPojo(String id) {
            this.id = id;
        }
    }

    /**
     * A sample implementation of the {@link BaseCarlosRepository} for use in testing
     */
    private static class TestRepository extends BaseCarlosRepository {
        public CompletableFuture<TestPojo> findById(Endpoint endpoint, String id) {
            IMProtocol.GetIncidentRequest request = IMProtocol.GetIncidentRequest.newBuilder()
                    .setId(id)
                    .build();
            IMProtocol.IncidentMessage message = IMProtocol.IncidentMessage.newBuilder()
                    .setType(IMProtocol.IncidentMessage.IncidentMessageType.GetIncidentRequest)
                    .setGetIncidentRequest(request)
                    .build();

            CompletableFuture<IMProtocol.IncidentMessage> future =
                    send(endpoint, message, IMProtocol.IncidentMessage.class);

            return future.thenApply(incidentMessage -> new TestPojo("INC-1"));
        }
    }
}
