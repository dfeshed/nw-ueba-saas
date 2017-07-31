package presidio.sdk.api.domain;

import fortscale.common.general.PresidioSchemas;
import fortscale.domain.core.AbstractAuditableDocument;

import java.time.Instant;
import java.util.List;

/**
 * Created by maors on 6/7/2017.
 */
public interface DataService {

    boolean store(List<? extends AbstractAuditableDocument> documents, PresidioSchemas presidioSchemas);

    List<? extends AbstractAuditableDocument> find(Instant startDate, Instant endDate, PresidioSchemas presidioSchemas);

    int clean(Instant startDate, Instant endDate, PresidioSchemas presidioSchemas);

    void cleanAll(PresidioSchemas presidioSchemas);

}
