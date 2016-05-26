package com.rsa.asoc.sa.ui.threat.web.socket;

import com.rsa.asoc.sa.ui.common.data.Request;
import com.rsa.asoc.sa.ui.common.data.Response;
import com.rsa.asoc.sa.ui.common.data.ResponseCode;
import com.rsa.asoc.sa.ui.common.service.WebSocketSubscriptionService;
import com.rsa.asoc.sa.ui.test.stomp.StompMessageUtils;
import com.rsa.asoc.sa.ui.test.stomp.TestingMessageChannel;
import com.rsa.asoc.sa.ui.test.stomp.TestingStompMessageHandler;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link CancelController}
 *
 * @author Abram Thielke
 * @since 10.6.0.0
 */
public class CancelControllerTest {

    private final ParameterizedTypeReference<Response<?>> responseType =
            new ParameterizedTypeReference<Response<?>>() {};

    private TestingStompMessageHandler testingStompMessageHandler;

    @Mock
    private WebSocketSubscriptionService webSocketSubscriptionService;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        testingStompMessageHandler =
                new TestingStompMessageHandler(new CancelController(webSocketSubscriptionService));
    }

    @Test
    public void testCancelRequest() {
        String id = UUID.randomUUID().toString();
        Request request = Request.newBuilder()
                .withId(id)
                .build();
        Message<?> message =
                StompMessageUtils.createMessage(StompCommand.SEND, "/ws/threats/cancel", "admin", request);

        testingStompMessageHandler.handleMessage(message);

        verify(webSocketSubscriptionService).cleanupRequest(any(MessageHeaders.class), eq(id));

        TestingMessageChannel outboundMessageChannel = testingStompMessageHandler.getOutboundChannel();
        assertEquals(1, outboundMessageChannel.getMessages().size());
        Message<Response<?>> reply = outboundMessageChannel.popMessage(responseType);

        StompHeaderAccessor replyHeaders = StompHeaderAccessor.wrap(reply);
        assertEquals("/user/admin/queue/threats/cancel", replyHeaders.getDestination());

        Response<?> response = reply.getPayload();
        assertEquals(ResponseCode.SUCCESS, response.getCode());
        assertNotNull(response.getRequest());
        assertEquals(id, response.getRequest().getId());
    }

    @Test
    public void testCancelRequestWithoutId() {
        Request request = new Request();
        Message<?> message =
                StompMessageUtils.createMessage(StompCommand.SEND, "/ws/threats/cancel", "admin", request);

        testingStompMessageHandler.handleMessage(message);

        verify(webSocketSubscriptionService, never()).cleanupRequest(any(MessageHeaders.class), anyString());

        TestingMessageChannel outboundMessageChannel = testingStompMessageHandler.getOutboundChannel();
        assertEquals(1, outboundMessageChannel.getMessages().size());
        Message<Response<?>> reply = outboundMessageChannel.popMessage(responseType);

        StompHeaderAccessor replyHeaders = StompHeaderAccessor.wrap(reply);
        assertEquals("/user/admin/queue/threats/cancel", replyHeaders.getDestination());

        Response<?> response = reply.getPayload();
        assertEquals(ResponseCode.SUCCESS, response.getCode());
        assertNotNull(response.getRequest());
    }
}
