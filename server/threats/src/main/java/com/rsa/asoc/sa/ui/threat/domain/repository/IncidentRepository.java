package com.rsa.asoc.sa.ui.threat.domain.repository;

import com.rsa.asoc.sa.ui.common.data.Request;
import com.rsa.asoc.sa.ui.common.endpoint.domain.bean.Endpoint;
import com.rsa.asoc.sa.ui.threat.domain.bean.Incident;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * A repository for retrieving and persisting {@link Incident} domain objects.
 *
 * @author Abram Thielke
 * @since 10.6.0.0
 */
public interface IncidentRepository {

    /**
     * Finds a single {@link Incident} by its identifier.
     *
     * @param endpoint the incident management endpoint to query
     * @param id the incident identifier to find
     * @return a {@link CompletableFuture} containing an {@link Optional} that possibly contains an {@link Incident}
     */
    CompletableFuture<Optional<Incident>> findById(Endpoint endpoint, String id);

    /**
     * Finds incidents that match the filters/criteria defined in the {@link Request}.
     *
     * @param endpoint the incident management endpoint to query
     * @param request the request containing the filtering criteria, sorting fields and pagination parameters
     * @return a {@link CompletableFuture} containing a subset of the matching incidents (defined by the pagination
     * parameters)
     */
    CompletableFuture<List<Incident>> find(Endpoint endpoint, Request request);

    /**
     * Finds number of incidents that match the filters/criteria defined in the {@link Request}.
     *
     * @param endpoint the incident management endpoint to query
     * @param request the request containing the filtering criteria, sorting fields and pagination parameters
     * @return a {@link CompletableFuture} containing the number of matching incidents
     */
    CompletableFuture<Long> count(Endpoint endpoint, Request request);
}
