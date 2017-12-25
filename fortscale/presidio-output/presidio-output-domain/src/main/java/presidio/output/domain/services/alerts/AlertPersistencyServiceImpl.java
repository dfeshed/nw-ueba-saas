package presidio.output.domain.services.alerts;

import edu.emory.mathcs.backport.java.util.Collections;
import fortscale.utils.elasticsearch.PresidioElasticsearchTemplate;
import fortscale.utils.logging.Logger;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import presidio.output.domain.records.alerts.Alert;
import presidio.output.domain.records.alerts.AlertQuery;
import presidio.output.domain.records.alerts.Indicator;
import presidio.output.domain.records.alerts.IndicatorEvent;
import presidio.output.domain.records.alerts.IndicatorSummary;
import presidio.output.domain.repositories.AlertRepository;
import presidio.output.domain.repositories.IndicatorEventRepository;
import presidio.output.domain.repositories.IndicatorRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class AlertPersistencyServiceImpl implements AlertPersistencyService {

    Logger logger = Logger.getLogger(AlertPersistencyServiceImpl.class);

    @Autowired
    private AlertRepository alertRepository;

    @Autowired
    private IndicatorRepository indicatorRepository;

    @Autowired
    private IndicatorEventRepository indicatorEventRepository;

    @Autowired
    private PresidioElasticsearchTemplate elasticsearchTemplate;

    public Alert save(Alert alert) {
        save(Collections.singletonList(alert));
        return alert;
    }

    public Iterable<Alert> save(List<Alert> alerts) {
        alerts.forEach(alert -> {
            alert.updateFieldsBeforeSave();
        });
        // atomic save for the entire alert entities

        // save alerts
        Iterable<Alert> savedAlerts = alertRepository.save(alerts);

        // save indicators
        List<Indicator> indicators = new ArrayList<Indicator>();
        alerts.stream()
                .filter(alert -> alert.getIndicators() != null)
                .forEach(alert -> indicators.addAll(alert.getIndicators()));
        if (CollectionUtils.isNotEmpty(indicators)) {
            indicatorRepository.save(indicators);
        }

        // save events
        List<IndicatorEvent> events = new ArrayList<IndicatorEvent>();
        indicators.stream()
                .filter(indicator -> indicator.getEvents() != null)
                .forEach(indicator -> events.addAll(indicator.getEvents()));
        if (CollectionUtils.isNotEmpty(events)) {
            indicatorEventRepository.save(events);
        }

        return savedAlerts;
    }

    public void delete(Alert alert) {
        // atomic delete for the entire alert entities

        // delete alert
        alertRepository.delete(alert);

        // delete indicators
        List<Indicator> indicators = indicatorRepository.removeByAlertId(alert.getId());

        // delete events
        indicators.forEach(indicator -> {
            indicatorEventRepository.removeByIndicatorId(indicator.getId());
        });
    }

    @Override
    public Alert findOne(String id) {
        return alertRepository.findOne(id);
    }

    @Override
    public Iterable<Alert> findAll() {
        return alertRepository.findAll();
    }

    @Override
    public Iterable<Alert> findAll(List<String> ids) {
        return alertRepository.findAll(ids);
    }

    @Override
    public Page<Alert> findByUserName(String userName, PageRequest pageRequest) {
        return alertRepository.findByUserName(userName, pageRequest);
    }

    @Override
    public Page<Alert> findByUserId(String userId, PageRequest pageRequest) {
        return alertRepository.findByUserId(userId, pageRequest);
    }

    @Override
    public Page<Alert> findById(String id, PageRequest pageRequest) {
        return alertRepository.findById(id, pageRequest);
    }

    @Override
    public Page<Alert> find(AlertQuery alertQuery) {
        return alertRepository.search(new AlertElasticsearchQueryBuilder(alertQuery).build());
    }

    @Override
    public Page<Alert> findByUserIdIn(Collection<String> userId, PageRequest pageRequest) {
        return alertRepository.findByUserIdIn(userId, pageRequest);
    }

    @Override
    public Indicator findIndicatorById(String indicatorId) {
        return indicatorRepository.findIndicatorById(indicatorId);
    }

    @Override
    public IndicatorSummary findIndicatorSummaryById(String indicatorId) {
        return indicatorRepository.findIndicatorSummaryById(indicatorId);
    }

    @Override
    public Page<Indicator> findIndicatorsByAlertId(String alertId, PageRequest pageRequest) {
        return indicatorRepository.findByAlertIdOrderByScoreContributionDesc(alertId, pageRequest);
    }

    @Override
    public Page<IndicatorSummary> findIndicatorsSummaryByAlertId(String alertId, PageRequest pageRequest) {
        return indicatorRepository.findIndicatorsSummaryByAlertId(alertId, pageRequest);
    }

    @Override
    public Page<IndicatorEvent> findIndicatorEventsByIndicatorId(String indicatorId, PageRequest pageRequest) {
        return indicatorEventRepository.findIndicatorEventsByIndicatorId(indicatorId, pageRequest);
    }

    @Override
    public List<Alert> removeByTimeRange(Instant startDate, Instant endDate) {
        List<Alert> removedAlerts = new ArrayList<Alert>();

        try (Stream<Alert> alerts = alertRepository.findByStartDateGreaterThanEqualAndEndDateLessThan(startDate.toEpochMilli(), endDate.toEpochMilli())) {
            alerts.forEach(alert -> {
                delete(alert);
                removedAlerts.add(alert);
            });
        }

        return removedAlerts;
    }

    @Override
    public List<Alert> removeByEndDAte(Instant endDate) {
        List<Alert> removedAlerts = new ArrayList<Alert>();

        try (Stream<Alert> alerts = alertRepository.findByEndDateLessThan(endDate.toEpochMilli())) {
            alerts.forEach(alert -> {
                delete(alert);
                removedAlerts.add(alert);
            });
        }

        return removedAlerts;
    }


    @Override
    public List<Alert> findByUserId(String userId) {
        List<Alert> alerts = new ArrayList<Alert>();
        try (Stream<Alert> stream = alertRepository.findByUserId(userId)) {
            alerts = stream.collect(Collectors.toList());
        }
        return alerts;
    }

    @Override
    public long countAlerts() {
        return alertRepository.count();
    }

    @Override
    public Indicator save(Indicator indicator) {
        return indicatorRepository.save(indicator);
    }
}