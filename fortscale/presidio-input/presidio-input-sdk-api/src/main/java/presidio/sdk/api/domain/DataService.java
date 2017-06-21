package presidio.sdk.api.domain;

import fortscale.domain.core.AbstractAuditableDocument;

import java.time.Instant;
import java.util.List;

/**
 * Created by maors on 6/7/2017.
 */
public interface DataService {

    boolean store(List<? extends AbstractAuditableDocument> documents);

    List<? extends AbstractAuditableDocument> find(Instant startDate, Instant endDate);

    int clean(Instant startDate, Instant endDate);

    void cleanAll();
}
