package presidio.output.domain.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import presidio.output.domain.records.alerts.Alert;

import java.util.stream.Stream;

public interface AlertRepository extends ElasticsearchRepository<Alert, String> {

    Page<Alert> findByEntityName(String entityName, Pageable pageable);

    Page<Alert> findByEntityDocumentId(String entityDocumentId, Pageable pageable);

    Stream<Alert> findByStartDateGreaterThanEqualAndEndDateLessThanEqualAndEntityType(long startDate, long endDate, String entityType); // the stream must be closed after usage

    Stream<Alert> findByStartDateGreaterThanEqualAndEndDateLessThanEqual(long startDate, long endDate); // the stream must be closed after usage

    Stream<Alert> findByEndDateLessThan(long endDate); // the stream must be closed after usage

    Stream<Alert> findByEndDateLessThanAndEntityType(long endDate, String entityType); // the stream must be closed after usage

    Stream<Alert> findByEntityDocumentId(String entityDocumentId); // the stream must be closed after usage

}