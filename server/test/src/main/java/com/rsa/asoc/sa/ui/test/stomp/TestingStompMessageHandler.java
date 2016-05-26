package com.rsa.asoc.sa.ui.test.stomp;

import org.springframework.context.support.StaticApplicationContext;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.support.SimpAnnotationMethodMessageHandler;

import java.util.Collections;

/**
 * Configures a {@link SimpAnnotationMethodMessageHandler} suitable for testing and allows access to the underlying
 * outbound message queue.
 *
 * @author Abram Thielke
 * @since 10.6.0.0
 */
public class TestingStompMessageHandler {

    private final TestingMessageChannel outboundChannel;
    private final TestingAnnotationMethodMessageHandler messageHandler;

    public TestingStompMessageHandler(Object ... controllers) {
        this(new TestingMessageChannel(), controllers);
    }

    public TestingStompMessageHandler(TestingMessageChannel outboundMessageChannel, Object ... controllers) {
        this.outboundChannel = outboundMessageChannel;
        TestingMessageChannel inboundChannel = new TestingMessageChannel();
        messageHandler = new TestingAnnotationMethodMessageHandler(inboundChannel, outboundChannel,
                new SimpMessagingTemplate(outboundChannel));

        for (Object controller : controllers) {
            messageHandler.registerHandler(controller);
        }
        messageHandler.setDestinationPrefixes(Collections.singleton("/ws"));
        messageHandler.setMessageConverter(new MappingJackson2MessageConverter());
        messageHandler.setApplicationContext(new StaticApplicationContext());
        messageHandler.afterPropertiesSet();
    }

    /**
     * Sends a request as if it were coming from a client, normally a web browser.
     */
    public void handleMessage(Message<?> message) throws MessagingException {
        messageHandler.handleMessage(message);
    }

    public TestingMessageChannel getOutboundChannel() {
        return outboundChannel;
    }

    /**
     * A {@link SimpAnnotationMethodMessageHandler} that provides an easy way to register method handlers
     * instead of scanning the application context.
     */
    public static class TestingAnnotationMethodMessageHandler extends SimpAnnotationMethodMessageHandler {

        public TestingAnnotationMethodMessageHandler(SubscribableChannel clientInboundChannel,
                MessageChannel clientOutboundChannel, SimpMessageSendingOperations brokerTemplate) {
            super(clientInboundChannel, clientOutboundChannel, brokerTemplate);
        }

        public void registerHandler(Object handler) {
            super.detectHandlerMethods(handler);
        }
    }

}
