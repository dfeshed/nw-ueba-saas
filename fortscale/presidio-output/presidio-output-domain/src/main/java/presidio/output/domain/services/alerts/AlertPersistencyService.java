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

    Page<Alert> findByEntityName(String entityName, PageRequest pageRequest);

    Page<Alert> findByEntityDocumentId(String entityDocumentId, PageRequest pageRequest);

    List<Alert> findByEntityDocumentId(String entityDocumentId);

    Page<Alert> find(AlertQuery alertQuery);

    Indicator findIndicatorById(String indicatorId);

    Page<Indicator> findIndicatorsByAlertId(String alertId, PageRequest pageRequest);

    Page<IndicatorEvent> findIndicatorEventsByIndicatorId(String indicatorId, PageRequest pageRequest);

    List<Alert> removeByTimeRangeAndEntityType(Instant startDate, Instant endDate, String entityType);

    Stream<Alert> findAlertsByDateAndEntityType(Instant startDate, Instant endDate, String entityType);

    Stream<Indicator> findIndicatorsAlertIds(List<String> alertIds);

    long countAlerts();

}