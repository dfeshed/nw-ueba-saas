package com.rsa.asoc.sa.ui.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.converter.DefaultContentTypeResolver;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

import java.util.List;

/**
 * Base web socket configuration that creates the STOMP endpoints and configures SockJS.
 */
@EnableWebSocketMessageBroker
public abstract class BaseWebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

    @Autowired
    private ObjectMapper objectMapper;

    protected abstract String getEndpointPrefix();

    @Bean
    public WebSocketSettings webSocketSettings() {
        return new WebSocketSettings();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/ws");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(getEndpointPath())
                .setAllowedOrigins("*")
                .withSockJS()
                .setHeartbeatTime(webSocketSettings().getSockjs().getHeartbeatInterval());
    }

    @Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
        DefaultContentTypeResolver resolver = new DefaultContentTypeResolver();
        resolver.setDefaultMimeType(MimeTypeUtils.APPLICATION_JSON);

        // Configure the mapper to use the global ObjectMapper instead of creating a new one
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setObjectMapper(objectMapper);
        converter.setContentTypeResolver(resolver);
        messageConverters.add(converter);

        return false;
    }

    /**
     * Ensures the provided prefix is valid and creates the fully-qualified STOMP endpoint path in the form
     * of: [prefix]/socket
     * @return the fully-qualified STOMP endpoint path
     */
    private String getEndpointPath() {
        String prefix = getEndpointPrefix();
        Preconditions.checkState(!Strings.isNullOrEmpty(prefix), "Endpoint prefix must be defined.");

        StringBuilder builder = new StringBuilder(prefix);
        if (!prefix.startsWith("/")) {
            builder.insert(0, "/");
        }
        if (!prefix.endsWith("/")) {
            builder.append("/");
        }

        builder.append("socket");

        return builder.toString();
    }
}
