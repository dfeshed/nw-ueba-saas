package com.rsa.asoc.sa.ui.common.endpoint;

import com.rsa.asoc.sa.ui.common.endpoint.domain.bean.Endpoint;
import com.rsa.asoc.sa.ui.common.endpoint.domain.bean.EndpointType;
import com.rsa.asoc.sa.ui.common.endpoint.domain.bean.GenericEndpoint;
import com.rsa.asoc.sa.ui.common.endpoint.domain.repository.ApplianceEndpointRepository;
import com.rsa.netwitness.carlos.transport.MessageEndpointFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Default implementation of {@link EndpointService}
 *
 * @author Jay Garala
 * @since 10.6.0
 */
@Service
public class DefaultEndpointService implements EndpointService {

    private final ApplianceEndpointRepository applianceEndpointRepository;
    private final MessageEndpointFactory messageEndpointFactory;

    @Autowired
    public DefaultEndpointService(ApplianceEndpointRepository applianceEndpointRepository,
            MessageEndpointFactory messageEndpointFactory) {
        this.applianceEndpointRepository = applianceEndpointRepository;
        this.messageEndpointFactory = messageEndpointFactory;
    }

    @Override
    public CompletableFuture<List<Endpoint>> getEndpointsByType(EndpointType type) {
        return applianceEndpointRepository.findEndpointsByType(type).thenApply((descriptors) ->
            descriptors.stream().map((desc) -> {
                GenericEndpoint genericEndpoint = new GenericEndpoint(desc.getApplianceDescriptor(), desc);
                genericEndpoint.setMessageEndpointFactory(messageEndpointFactory);
                return genericEndpoint;
            }).collect(Collectors.toList()));
    }
}
