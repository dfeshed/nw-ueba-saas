package presidio.output.domain.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import presidio.output.domain.records.alerts.Indicator;
import presidio.output.domain.records.alerts.IndicatorSummary;

import java.util.List;
import java.util.stream.Stream;

public interface IndicatorRepository extends ElasticsearchRepository<Indicator, String> {

    Indicator findIndicatorById(String indicatorId);

    IndicatorSummary findIndicatorSummaryById(String indicatorId);

    Page<Indicator> findByAlertId(String alertId, Pageable pageRequest);

    Page<IndicatorSummary> findIndicatorsSummaryByAlertId(String alertId, Pageable pageRequest);

    Iterable<Indicator> findById(String id);

    List<Indicator> removeByAlertId(String alertId);

    Stream<Indicator> findByAlertId(String alertId);

}