package presidio.sdk.api.domain;

import fortscale.common.general.Schema;
import fortscale.domain.core.AbstractAuditableDocument;

import java.time.Instant;
import java.util.List;

/**
 * Created by maors on 6/7/2017.
 */
public interface DataService {

    boolean store(List<? extends AbstractAuditableDocument> documents, Schema schema);

    List<? extends AbstractAuditableDocument> find(Instant startDate, Instant endDate, Schema schema);

    int clean(Instant startDate, Instant endDate, Schema schema);

    void cleanAll(Schema schema);

}
