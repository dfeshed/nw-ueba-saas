package com.rsa.asoc.sa.ui.common.endpoint.domain.repository;

import com.rsa.asoc.sa.ui.common.endpoint.domain.bean.EndpointType;
import com.rsa.netwitness.carlos.common.asg.domain.bean.ApplianceDescriptor;
import com.rsa.netwitness.carlos.common.asg.domain.bean.EndpointDescriptor;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Represents a Repository for Appliances and Endpoints.  Currently read-only operations are permitted.
 */
public interface ApplianceEndpointRepository  {

    /**
     * @return all appliances in this repository
     */
    CompletableFuture<List<ApplianceDescriptor>> getAppliances();

    /**
     * @param id Appliance Id
     * @return An appliance if found else null
     */
    CompletableFuture<Optional<ApplianceDescriptor>> findById(String id);

    /**
     * @return All endpoints
     */
    CompletableFuture<List<EndpointDescriptor>> getEndpoints();

    /**
     * @param type Type of endpoint
     * @return Endpoints if found else empty list
     */
    CompletableFuture<List<EndpointDescriptor>> findEndpointsByType(EndpointType type);
}
