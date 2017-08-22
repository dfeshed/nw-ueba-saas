package presidio.output.domain.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import presidio.output.domain.records.alerts.Alert;

public interface AlertRepository extends ElasticsearchRepository<Alert, String> {

    Page<Alert> findByUserName(String userName, Pageable pageable);

    Page<Alert> findByClassification(String classification, Pageable pageable);

}