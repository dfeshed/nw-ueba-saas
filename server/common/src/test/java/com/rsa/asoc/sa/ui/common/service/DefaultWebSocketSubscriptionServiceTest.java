package com.rsa.asoc.sa.ui.common.service;

import com.rsa.asoc.sa.ui.common.data.Request;
import org.junit.Before;
import org.junit.Test;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Unit test for {#WebSocketSubscriptionService}
 *
 * @author Jay Garala
 * @since 10.6.0
 */
public class DefaultWebSocketSubscriptionServiceTest {

    private static final int DEFAULT_COUNTDOWN_TIMEOUT = 10_000;

    private DefaultWebSocketSubscriptionService webSocketSubscriptionService;

    @Before
    public void init() {
        webSocketSubscriptionService = new DefaultWebSocketSubscriptionService();
    }

    @Test
    public void testExecuteOnDisconnectEvent() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        final String sessionId = UUID.randomUUID().toString();
        final String subscriptionId = UUID.randomUUID().toString();
        final MessageHeaders headers = createMessageHeaders(sessionId, subscriptionId);
        final Request request = Request.newBuilder()
                .withId(UUID.randomUUID().toString())
                .build();

        webSocketSubscriptionService.submit(headers, request, latch::countDown);

        webSocketSubscriptionService.onApplicationEvent(createDisconnectEvent(sessionId));

        boolean finished = latch.await(DEFAULT_COUNTDOWN_TIMEOUT, TimeUnit.MILLISECONDS);
        assertTrue(finished);

        Collection<String> taskIds = webSocketSubscriptionService.getTaskIdsForSession(sessionId);
        assertTrue(taskIds.isEmpty());
    }

    @Test
    public void testExecuteOnFutureCompletion() throws Exception {
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        final String sessionId = UUID.randomUUID().toString();
        final String subscriptionId = UUID.randomUUID().toString();
        final MessageHeaders headers = createMessageHeaders(sessionId, subscriptionId);
        final Request request = Request.newBuilder()
                .withId(UUID.randomUUID().toString())
                .build();

        webSocketSubscriptionService.submit(headers, request, future);

        future.complete(true);

        Collection<String> taskIds = webSocketSubscriptionService.getTaskIdsForSession(sessionId);
        assertTrue(taskIds.isEmpty());
    }

    @Test
    public void testExecuteOnFutureCompletionWithNoRequest() throws Exception {
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        final String sessionId = UUID.randomUUID().toString();
        final String subscriptionId = UUID.randomUUID().toString();
        final MessageHeaders headers = createMessageHeaders(sessionId, subscriptionId);

        webSocketSubscriptionService.submit(headers, future);

        future.complete(true);

        Collection<String> taskIds = webSocketSubscriptionService.getTaskIdsForSession(sessionId);
        assertTrue(taskIds.isEmpty());
    }

    @Test
    public void testExecuteOnUnsubscribeEvent() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        final String sessionId = UUID.randomUUID().toString();
        final String subscriptionId = UUID.randomUUID().toString();
        final MessageHeaders headers = createMessageHeaders(sessionId, subscriptionId);
        final Request request = Request.newBuilder()
                .withId(UUID.randomUUID().toString())
                .build();

        webSocketSubscriptionService.submit(headers, request, latch::countDown);

        webSocketSubscriptionService.onApplicationEvent(createUnsubscribeEvent(sessionId, subscriptionId));

        boolean finished = latch.await(DEFAULT_COUNTDOWN_TIMEOUT, TimeUnit.MILLISECONDS);
        assertTrue(finished);

        Collection<String> taskIds = webSocketSubscriptionService.getTaskIdsForSession(sessionId);
        assertTrue(taskIds.isEmpty());
    }

    @Test
    public void testExecuteOnUnsubscribeEventWithNoRequest() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        final String sessionId = UUID.randomUUID().toString();
        final String subscriptionId = UUID.randomUUID().toString();
        final MessageHeaders headers = createMessageHeaders(sessionId, subscriptionId);

        webSocketSubscriptionService.submit(headers, latch::countDown);

        webSocketSubscriptionService.onApplicationEvent(createUnsubscribeEvent(sessionId, subscriptionId));

        boolean finished = latch.await(DEFAULT_COUNTDOWN_TIMEOUT, TimeUnit.MILLISECONDS);
        assertTrue(finished);

        Collection<String> taskIds = webSocketSubscriptionService.getTaskIdsForSession(sessionId);
        assertTrue(taskIds.isEmpty());
    }

    @Test
    public void testNoExecutionOnDifferentUnsubscribeEvent() throws Exception {
        final String sessionId = UUID.randomUUID().toString();
        final String subscriptionId = UUID.randomUUID().toString();
        final MessageHeaders headers = createMessageHeaders(sessionId, subscriptionId);
        final Request request = Request.newBuilder()
                .withId(UUID.randomUUID().toString())
                .build();

        Runnable runnable = mock(Runnable.class);
        webSocketSubscriptionService.submit(headers, request, runnable);

        webSocketSubscriptionService.onApplicationEvent(
                createUnsubscribeEvent(sessionId, UUID.randomUUID().toString()));

        verify(runnable, never()).run();

        Collection<String> taskIds = webSocketSubscriptionService.getTaskIdsForSession(sessionId);
        assertEquals(1, taskIds.size());
    }

    @Test
    public void testManualCleanupByTaskId() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        final String sessionId = UUID.randomUUID().toString();
        final String subscriptionId = UUID.randomUUID().toString();
        final MessageHeaders headers = createMessageHeaders(sessionId, subscriptionId);
        final Request request = Request.newBuilder()
                .withId(UUID.randomUUID().toString())
                .build();


        String taskId = webSocketSubscriptionService.submit(headers, request, latch::countDown);

        webSocketSubscriptionService.cleanupTask(sessionId, taskId);

        boolean finished = latch.await(DEFAULT_COUNTDOWN_TIMEOUT, TimeUnit.MILLISECONDS);
        assertTrue(finished);

        Collection<String> taskIds = webSocketSubscriptionService.getTaskIdsForSession(sessionId);
        assertEquals(0, taskIds.size());
    }

    @Test
    public void testManualCleanupByRequestId() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        final String sessionId = UUID.randomUUID().toString();
        final String subscriptionId = UUID.randomUUID().toString();
        final MessageHeaders headers = createMessageHeaders(sessionId, subscriptionId);
        final Request request = Request.newBuilder()
                .withId(UUID.randomUUID().toString())
                .build();

        webSocketSubscriptionService.submit(headers, request, latch::countDown);

        webSocketSubscriptionService.cleanupRequest(sessionId, request.getId());

        boolean finished = latch.await(DEFAULT_COUNTDOWN_TIMEOUT, TimeUnit.MILLISECONDS);
        assertTrue(finished);

        Collection<String> taskIds = webSocketSubscriptionService.getTaskIdsForSession(sessionId);
        assertEquals(0, taskIds.size());
    }

    @Test
    public void testAcceptsEvents() {
        assertTrue(webSocketSubscriptionService.supportsEventType(SessionDisconnectEvent.class));
        assertTrue(webSocketSubscriptionService.supportsEventType(SessionUnsubscribeEvent.class));
        assertFalse(webSocketSubscriptionService.supportsEventType(SessionConnectEvent.class));
    }

    private SessionDisconnectEvent createDisconnectEvent(String sessionId) {
        StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.DISCONNECT);
        headers.setSessionId(sessionId);

        Message<byte[]> message = MessageBuilder.createMessage(new byte[0], headers.toMessageHeaders());

        return new SessionDisconnectEvent(this, message, sessionId, CloseStatus.NORMAL);
    }

    private SessionUnsubscribeEvent createUnsubscribeEvent(String sessionId, String subscriptionId) {
        StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.UNSUBSCRIBE);
        headers.setSessionId(sessionId);
        headers.setSubscriptionId(subscriptionId);

        Message<byte[]> message = MessageBuilder.createMessage(new byte[0], headers.toMessageHeaders());

        return new SessionUnsubscribeEvent(this, message);
    }

    private MessageHeaders createMessageHeaders(String sessionId, String subscriptionId) {
        SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.create();
        headers.setSessionId(sessionId);
        headers.setSubscriptionId(subscriptionId);
        return headers.toMessageHeaders();
    }
}