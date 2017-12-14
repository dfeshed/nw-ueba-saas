package presidio.output.domain.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import presidio.output.domain.records.alerts.Indicator;
import presidio.output.domain.records.alerts.IndicatorEvent;

import java.util.List;

public interface IndicatorEventRepository extends ElasticsearchRepository<IndicatorEvent, String> {
    Page<IndicatorEvent> findIndicatorEventsByIndicatorId(String indicatorId, Pageable pageRequest);

    List<Indicator> removeByIndicatorId(String indicatorId);
}
