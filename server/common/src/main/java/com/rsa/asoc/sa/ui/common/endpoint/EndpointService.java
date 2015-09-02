package com.rsa.asoc.sa.ui.common.endpoint;


import com.rsa.asoc.sa.ui.common.endpoint.domain.bean.Endpoint;
import com.rsa.asoc.sa.ui.common.endpoint.domain.bean.EndpointType;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Service to retrieve Endpoints from the configured repository.
 */
public interface EndpointService {

    /**
     * Retrieve all endpoints with specified type
     * @param type Endpoint type
     * @return List of endpoints or empty list
     */
    CompletableFuture<List<Endpoint>> getEndpointsByType(EndpointType type);

}
