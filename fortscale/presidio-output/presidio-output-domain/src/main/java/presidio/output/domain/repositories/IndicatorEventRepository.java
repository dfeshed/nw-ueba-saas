package presidio.output.domain.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import presidio.output.domain.records.alerts.IndicatorEvent;

import java.util.List;
import java.util.stream.Stream;

public interface IndicatorEventRepository extends ElasticsearchRepository<IndicatorEvent, String> {
    Page<IndicatorEvent> findIndicatorEventsByIndicatorIdOrderByEventTimeDesc(String indicatorId, Pageable pageRequest);

    Stream<IndicatorEvent> findByIndicatorId(String indicatorId);

    List<IndicatorEvent> removeByIndicatorId(String indicatorId);
}
