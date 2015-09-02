package com.rsa.asoc.sa.ui.common.endpoint.domain.bean;

import com.google.protobuf.Message;
import com.rsa.netwitness.carlos.transport.MessageChannel;
import com.rsa.netwitness.carlos.transport.MessageEndpoint;

import java.net.URI;

/**
 * Endpoint to a Security Analytics Service
 *
 * @author Jay Garala
 * @since 10.6.0
 */
public interface Endpoint {

    /**
     * URI to generate a message endpoint
     * @return URI
     */
    URI getUri();

    /**
     * Creates a message channel to the endpoint for the specified {@link Message} type
     *
     * @param clazz An implementation of {@link Message}
     * @param <T> An implementation of {@link Message}
     * @return {@link MessageChannel}
     * @throws Exception
     *
     */
    <T extends Message> MessageChannel<T> getMessageChannel(Class<T> clazz) throws Exception;

    /**
     * {@link MessageEndpoint} for this endpoint
     *
     * @return A message endpoint {@link MessageEndpoint}
     * @throws Exception The URI is invalid or {@link com.rsa.netwitness.carlos.transport.MessageEndpointFactory}
     * is unable to connect to the endpoint.
     */
    MessageEndpoint getMessageEndpoint() throws Exception;

}
