package com.rsa.asoc.sa.ui.threat.domain.repository;

import com.rsa.asoc.sa.ui.common.data.Request;
import com.rsa.asoc.sa.ui.common.domain.repository.BaseCarlosRepository;
import com.rsa.asoc.sa.ui.common.endpoint.domain.bean.Endpoint;
import com.rsa.asoc.sa.ui.common.protobuf.ProtocolBufferUtils;
import com.rsa.asoc.sa.ui.common.protobuf.RepositoryProtocolUtils;
import com.rsa.asoc.sa.ui.threat.domain.bean.Incident;
import com.rsa.netwitness.carlos.plist.PropertyListProtocol;
import com.rsa.netwitness.im.IMProtocol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * A {@link IncidentRepository} that communicates to the IM service via CARLOS Transport + Protocol Buffers.
 *
 * @author Abram Thielke
 * @since 10.6.0.0
 */
@Repository
public class CarlosIncidentRepository extends BaseCarlosRepository implements IncidentRepository {

    private final ConversionService conversionService;

    @Autowired
    public CarlosIncidentRepository(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Override
    public CompletableFuture<Optional<Incident>> findById(Endpoint endpoint, String id) {
        IMProtocol.IncidentMessage message = IMProtocol.IncidentMessage.newBuilder()
                .setType(IMProtocol.IncidentMessage.IncidentMessageType.GetIncidentRequest)
                .setGetIncidentRequest(IMProtocol.GetIncidentRequest.newBuilder()
                        .setId(id))
                .build();

        CompletableFuture<IMProtocol.IncidentMessage> future =
                send(endpoint, message, IMProtocol.IncidentMessage.class);

        // The incident management service throws a NullPointerException when an incident with the given
        // identifier cannot be found. Most likely a bug in IM.
        future = future.exceptionally((throwable) -> {
            if (throwable instanceof NullPointerException) {
                return null;
            }
            throw new RuntimeException(throwable);
        });

        return future.thenApply(incidentMessage -> {
            PropertyListProtocol.Dictionary dictionary = null;
            if (incidentMessage != null && incidentMessage.hasGetIncidentResponse()
                    && incidentMessage.getGetIncidentResponse().hasIncident()) {
                dictionary = incidentMessage.getGetIncidentResponse().getIncident();
            }

            return dictionary != null
                    ? Optional.of(conversionService.convert(dictionary, Incident.class))
                    : Optional.empty();
        });
    }

    @Override
    public CompletableFuture<List<Incident>> find(Endpoint endpoint, Request request) {
        IMProtocol.IncidentMessage message = IMProtocol.IncidentMessage.newBuilder()
                .setType(IMProtocol.IncidentMessage.IncidentMessageType.FindIncidentsRequest)
                .setFindIncidentsRequest(IMProtocol.FindIncidentsRequest.newBuilder()
                        .setPagination(RepositoryProtocolUtils.buildPagination(request))
                        .setSorting(RepositoryProtocolUtils.buildSorting(request))
                        .setCriteria(RepositoryProtocolUtils.buildCriteria(request)))
                .build();

        CompletableFuture<IMProtocol.IncidentMessage> future =
                send(endpoint, message, IMProtocol.IncidentMessage.class);

        return future.thenApply(incidentMessage ->
            incidentMessage.getFindIncidentsResponse().getIncidentList().stream()
                    .map(m -> conversionService.convert(m, Incident.class))
                    .collect(Collectors.toList()));
    }

    @Override
    public CompletableFuture<Long> count(Endpoint endpoint, Request request) {
        // Setting the group by property to a non-existent value so the total count is returned instead
        // grouped values.
        IMProtocol.IncidentMessage message = IMProtocol.IncidentMessage.newBuilder()
                .setType(IMProtocol.IncidentMessage.IncidentMessageType.GetIncidentCountByGroupRequest)
                .setGetIncidentCountByGroupRequest(IMProtocol.GetIncidentCountByGroupRequest.newBuilder()
                        .setPropertyName("_does_not_exist_")
                        .setCriteria(RepositoryProtocolUtils.buildCriteria(request)))
                .build();

        CompletableFuture<IMProtocol.IncidentMessage> future =
                send(endpoint, message, IMProtocol.IncidentMessage.class);

        return future.thenApply(incidentMessage -> {
            PropertyListProtocol.Dictionary dictionary =
                    incidentMessage.getGetIncidentCountByGroupResponse().getGroupedIncidentCount();

            Integer count = ProtocolBufferUtils.getDictionaryValue(dictionary, "NOT_SET", Integer.class, 0);
            return Long.valueOf(count);
        });
    }
}
