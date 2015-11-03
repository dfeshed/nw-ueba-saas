package com.rsa.asoc.sa.ui.common.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.web.socket.messaging.AbstractSubProtocolEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.when;

/**
 * Unit test for {#WebSocketSubscriptionService}
 *
 * @author Jay Garala
 * @since 10.6.0
 */
public class WebSocketSubscriptionServiceTest {

    private static final int DEFAULT_COUNTDOWN_TIMEOUT = 10_000;

    @Mock
    SessionUnsubscribeEvent sessionUnsubscribeEvent;

    @Mock
    SessionDisconnectEvent sessionDisconnectEvent;

    private WebSocketSubscriptionService webSocketSubscriptionService;

    @Before
    public void init() throws NoSuchFieldException, IllegalAccessException {
        MockitoAnnotations.initMocks(this);

        webSocketSubscriptionService = new WebSocketSubscriptionService();
    }

    @Test
    public void testDisconnecteEventForSubmitIds() throws Exception {
        testSubmitIds(sessionDisconnectEvent);
    }

    @Test
    public void testUnsubscribeEventForSubmitIds() throws Exception {
        testSubmitIds(sessionUnsubscribeEvent);
    }

    @Test
    public void testDisconnecteEventForSubmitMessageHeaders() throws Exception {
        testSubmitHeader(sessionDisconnectEvent);
    }

    @Test
    public void testUnsubscribeEventForSubmitMessageHeaders() throws Exception {
        testSubmitHeader(sessionUnsubscribeEvent);
    }

    public void testSubmitIds(AbstractSubProtocolEvent event) throws Exception {

        CountDownLatch latch = new CountDownLatch(1);
        Runnable runnable = latch::countDown;

        when(event.getMessage()).thenReturn(createMessage("1234", "aaa"));
        webSocketSubscriptionService.submit("1234", "aaa", runnable);
        webSocketSubscriptionService.onApplicationEvent(event);

        boolean finished = latch.await(DEFAULT_COUNTDOWN_TIMEOUT, TimeUnit.MILLISECONDS);
        Assert.assertTrue(finished);
    }

    public void testSubmitHeader(AbstractSubProtocolEvent event) throws Exception {

        CountDownLatch latch = new CountDownLatch(1);
        Runnable runnable = latch::countDown;

        when(event.getMessage()).thenReturn(createMessage("1234", "bbb"));
        webSocketSubscriptionService.submit(createMessageHeaders("1234", "bbb"), runnable);
        webSocketSubscriptionService.onApplicationEvent(event);

        boolean finished = latch.await(DEFAULT_COUNTDOWN_TIMEOUT, TimeUnit.MILLISECONDS);
        Assert.assertTrue(finished);
    }

    private MessageHeaders createMessageHeaders(String sessionId, String subscriptionId) {
        Map<String, Object> headers = new HashMap<>();
        headers.put(SimpMessageHeaderAccessor.SESSION_ID_HEADER, sessionId);
        headers.put(SimpMessageHeaderAccessor.SUBSCRIPTION_ID_HEADER, subscriptionId);

        return new MessageHeaders(headers);
    }

    private Message<byte[]> createMessage(String sessionId, String subscriptionId) throws UnsupportedEncodingException {
        return new GenericMessage<>("FOO".getBytes("UTF-8"), createMessageHeaders(sessionId, subscriptionId));
    }
}