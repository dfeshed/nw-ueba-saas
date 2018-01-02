package presidio.output.domain.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import presidio.output.domain.records.alerts.Alert;

import java.util.Collection;
import java.util.stream.Stream;

public interface AlertRepository extends ElasticsearchRepository<Alert, String> {

    Page<Alert> findByUserName(String userName, Pageable pageable);

    Page<Alert> findByUserId(String userId, Pageable pageable);

    Page<Alert> findById(String Id, Pageable pageable);

    Page<Alert> findByUserIdIn(Collection<String> ids, Pageable pageable);

    Stream<Alert> findByStartDateGreaterThanEqualAndEndDateLessThanEqual(long startDate, long endDate); // the stream must be closed after usage

    Stream<Alert> findByEndDateLessThan(long endDate); // the stream must be closed after usage

    Stream<Alert> findByUserId(String userId); // the stream must be closed after usage

}