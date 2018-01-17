package presidio.output.domain.services.alerts;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import presidio.output.domain.records.alerts.Alert;
import presidio.output.domain.records.alerts.AlertQuery;
import presidio.output.domain.records.alerts.Indicator;
import presidio.output.domain.records.alerts.IndicatorEvent;
import presidio.output.domain.records.alerts.IndicatorSummary;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public interface AlertPersistencyService {

    Alert save(Alert alert);

    Indicator save(Indicator indicator);

    Iterable<Alert> save(List<Alert> alerts);

    void deleteAlertAndIndicators(Alert alert);

    Alert findOne(String id);

    Iterable<Alert> findAll();

    Iterable<Alert> findAll(List<String> ids);

    Page<Alert> findByUserName(String userName, PageRequest pageRequest);

    Page<Alert> findByUserId(String userId, PageRequest pageRequest);

    List<Alert> findByUserId(String userId);

    Page<Alert> findByUserIdIn(Collection<String> userId, PageRequest pageRequest);

    Page<Alert> findById(String id, PageRequest pageRequest);

    Page<Alert> find(AlertQuery alertQuery);

    Indicator findIndicatorById(String indicatorId);

    IndicatorSummary findIndicatorSummaryById(String indicatorId);

    Page<Indicator> findIndicatorsByAlertId(String alertId, PageRequest pageRequest);

    Page<IndicatorSummary> findIndicatorsSummaryByAlertId(String alertId, PageRequest pageRequest);

    Page<IndicatorEvent> findIndicatorEventsByIndicatorId(String indicatorId, PageRequest pageRequest);

    List<Alert> removeByTimeRange(Instant startDate, Instant endDate);

    Stream<Alert> findAlertsByDate(Instant startDate, Instant endDate);

    Stream<Indicator> findIndicatorByDate(Instant startDate, Instant endDate);

    List<IndicatorEvent> findIndicatorEventByIndicatorId(String indicatorId);

    long countAlerts();

}