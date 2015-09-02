package com.rsa.asoc.sa.ui.common.test.stomp;

import com.google.common.base.Preconditions;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.support.SimpAnnotationMethodMessageHandler;
import org.springframework.messaging.support.AbstractSubscribableChannel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Configures a {@link SimpAnnotationMethodMessageHandler} suitable for testing and allows access to the underlying
 * outbound message queue.
 *
 * @author Abram Thielke
 * @since 10.6.0.0
 */
public class TestingStompMessageHandler {

    private final TestingMessageChannel outboundChannel = new TestingMessageChannel();
    private final TestingAnnotationMethodMessageHandler messageHandler;

    public TestingStompMessageHandler(Object ... controllers) {
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

    /**
     * Returns the underlying outbound message queue.
     */
    public List<Message<?>> getOutboundMessages() {
        return outboundChannel.getMessages();
    }

    /**
     * Removes the first message from the outbound message queue.
     */
    public Message<?> popOutboundMessage() {
        Preconditions.checkState(!outboundChannel.getMessages().isEmpty(), "No messages to retrieve");
        return outboundChannel.getMessages().remove(0);
    }

    /**
     * Removes the first message from the outbound message queue and casts it to the given type.
     */
    public <T> Message<T> popOutboundMessage(ParameterizedTypeReference<T> type) {
        return (Message<T>) popOutboundMessage();
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

    /**
     * A {@link SubscribableChannel} that queues the messages to be sent in a list, instead of actually attempting
     * to deliver them.  Messages can be retrieved from the queue easily for tests.
     */
    public static class TestingMessageChannel extends AbstractSubscribableChannel {

        private final List<Message<?>> messages = new ArrayList<>();

        public List<Message<?>> getMessages() {
            return messages;
        }

        @Override
        protected boolean sendInternal(Message<?> message, long timeout) {
            messages.add(message);
            return true;
        }
    }
}
