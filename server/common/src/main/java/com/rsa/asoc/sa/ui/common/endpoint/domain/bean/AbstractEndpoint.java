package com.rsa.asoc.sa.ui.common.endpoint.domain.bean;

import com.google.common.base.Joiner;
import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import com.google.protobuf.Message;
import com.rsa.netwitness.carlos.common.asg.domain.bean.ApplianceDescriptor;
import com.rsa.netwitness.carlos.common.asg.domain.bean.EndpointDescriptor;
import com.rsa.netwitness.carlos.transport.MessageChannel;
import com.rsa.netwitness.carlos.transport.MessageEndpoint;
import com.rsa.netwitness.carlos.transport.MessageEndpointFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * Base Endpoint containing logic for creating message endpoint
 *
 * @author Jay Garala
 * @since 10.6.0
 */
public abstract class AbstractEndpoint implements Endpoint {

    private static final String CARLOS_SECURE_PARAM = "carlos.useSSL";
    private static final String CARLOS_DISPATCH_POOL = "carlos.dispatch.pool";
    private static final int DEFAULT_CARLOS_DISPATCH_POOL_SIZE =
            Integer.getInteger(CARLOS_DISPATCH_POOL, 1);
    private static final String CARLOS_DISPATCH_QUEUE = "carlos.dispatch.queue";
    private static final int DEFAULT_CARLOS_DISPATCH_QUEUE_SIZE =
            Integer.getInteger(CARLOS_DISPATCH_QUEUE, Integer.MAX_VALUE);

    private final ApplianceDescriptor applianceDescriptor;
    private final EndpointDescriptor endpointDescriptor;

    private MessageEndpointFactory messageEndpointFactory;

    public AbstractEndpoint(ApplianceDescriptor applianceDescriptor,
            EndpointDescriptor endpointDescriptor) {
        this.applianceDescriptor = applianceDescriptor;
        this.endpointDescriptor = endpointDescriptor;
    }

    public void setMessageEndpointFactory(MessageEndpointFactory messageEndpointFactory) {
        this.messageEndpointFactory = messageEndpointFactory;
    }

    @Override
    public URI getUri() {
        EndpointType type = EndpointType.valueOf(getEndpointDescriptor().getServiceType().name());

        try {
            String parameters = getUriQueryParameters();

            return new URI(type.getTransportType().getTransport(), null,
                    endpointDescriptor.getApplianceDescriptor().getHost(),
                    endpointDescriptor.getPort(), null, parameters, null);
        } catch (URISyntaxException e) {
            throw Throwables.propagate(e);
        }
    }

    @Override
    public MessageEndpoint getMessageEndpoint() throws Exception {
        return messageEndpointFactory.createClient(getUri());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Message> MessageChannel<T> getMessageChannel(Class<T> clazz) throws Exception {
        MessageEndpoint endpoint = getMessageEndpoint();
        if (!endpoint.isRunning()) {
            endpoint.start();
        }
        return endpoint.getChannel(clazz);
    }

    public ApplianceDescriptor getApplianceDescriptor() {
        return applianceDescriptor;
    }

    public EndpointDescriptor getEndpointDescriptor() {
        return endpointDescriptor;
    }

    private String getUriQueryParameters() {
        Map<String, Object> parameters = getConnectionParameters();

        StringBuilder sb = new StringBuilder();
        Joiner.on("&").withKeyValueSeparator("=").appendTo(sb, parameters);
        return sb.toString();
    }

    private Map<String, Object> getConnectionParameters() {
        Map<String, Object> parameters = Maps.newHashMap();

        parameters.put(CARLOS_DISPATCH_POOL, DEFAULT_CARLOS_DISPATCH_POOL_SIZE);
        parameters.put(CARLOS_DISPATCH_QUEUE, DEFAULT_CARLOS_DISPATCH_QUEUE_SIZE);
        parameters.put(CARLOS_SECURE_PARAM, endpointDescriptor.isSecure());

        // Ignore if the endpoint is reachable, we manually test it later
        parameters.put("carlos.ignoreIsReachable", true);

        return parameters;
    }
}