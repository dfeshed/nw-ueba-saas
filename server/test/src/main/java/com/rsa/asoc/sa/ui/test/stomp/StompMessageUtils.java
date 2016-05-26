package com.rsa.asoc.sa.ui.test.stomp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.authentication.TestingAuthenticationToken;

import java.util.HashMap;

/**
 * Utilities to assist in creating and verifying STOMP-based messages.
 *
 * @author Abram Thielke
 * @since 10.6.0.0
 */
public final class StompMessageUtils {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private StompMessageUtils() {}

    /**
     * Creates a STOMP {@link Message} of the given command with the default headers.
     *
     * @param command the STOMP command or type of message to create
     * @param destination the topic or queue this message will be sent to
     * @param user the user sending the request
     * @param payload the payload which will be converted to JSON before being sent
     * @return a STOMP message with the given attributes
     */
    public static Message<?> createMessage(StompCommand command, String destination, String user, Object payload) {
        StompHeaderAccessor headers = StompHeaderAccessor.create(command);
        headers.setDestination(destination);
        headers.setSessionId("0");
        headers.setUser(new TestingAuthenticationToken(user, null));
        headers.setSessionAttributes(new HashMap<>());

        try {
            byte[] json = MAPPER.writeValueAsBytes(payload);
            return MessageBuilder.withPayload(json)
                    .setHeaders(headers)
                    .build();
        }
        catch (JsonProcessingException e) {
            throw Throwables.propagate(e);
        }
    }

    /**
     * Returns the user-specific destination for the given username and suffix.
     *
     * @param username the username of the targeted user
     * @param destination the destination suffix
     * @return the fully-qualified user destination
     */
    public static String getUserDestination(String username, String destination) {
        if (destination.startsWith("/")) {
            destination = destination.substring(1, destination.length());
        }
        return String.format("/user/%s/%s", username, destination);
    }
}
