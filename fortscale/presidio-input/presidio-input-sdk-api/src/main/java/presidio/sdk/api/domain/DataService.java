package presidio.sdk.api.domain;

import fortscale.common.general.DataSource;
import fortscale.domain.core.AbstractAuditableDocument;

import java.time.Instant;
import java.util.List;

/**
 * Created by maors on 6/7/2017.
 */
public interface DataService {

    boolean store(List<? extends AbstractAuditableDocument> documents, DataSource dataSource);

    List<? extends AbstractAuditableDocument> find(Instant startDate, Instant endDate, DataSource dataSource);

    int clean(Instant startDate, Instant endDate, DataSource dataSource);

    void cleanAll(DataSource dataSource);

}
