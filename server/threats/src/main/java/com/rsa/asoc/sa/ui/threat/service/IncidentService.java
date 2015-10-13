package com.rsa.asoc.sa.ui.threat.service;

import com.rsa.asoc.sa.ui.common.data.Request;
import com.rsa.asoc.sa.ui.threat.domain.bean.Incident;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * A service for interacting with {@link Incident} domain objects.
 *
 * @author Abram Thielke
 * @since 10.6.0.0
 */
public interface IncidentService {

    /**
     * Finds a single incident with the given identifier.
     *
     * @return a {@link CompletableFuture} with an {@link Optional} that may contain an {@link Incident} with the
     * matching id
     */
    CompletableFuture<Optional<Incident>> findIncidentById(String id);

    /**
     * Finds incidents that match the filters/criteria defined in the {@link Request}.
     *
     * @param request the request containing the filtering criteria, sorting fields and pagination parameters
     * @return a {@link CompletableFuture} containing a subset of the matching incidents (defined by the pagination
     * parameters)
     */
    CompletableFuture<List<Incident>> findIncidents(Request request);

    /**
     * Finds number of incidents that match the filters/criteria defined in the {@link Request}.
     *
     * @param request the request containing the filtering criteria, sorting fields and pagination parameters
     * @return a {@link CompletableFuture} containing the number of matching incidents
     */
    CompletableFuture<Long> countIncidents(Request request);
}
