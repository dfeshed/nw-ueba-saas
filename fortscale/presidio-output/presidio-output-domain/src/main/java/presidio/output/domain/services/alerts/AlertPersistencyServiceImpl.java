package presidio.output.domain.services.alerts;

import com.google.common.collect.Iterables;
import edu.emory.mathcs.backport.java.util.Collections;
import fortscale.utils.elasticsearch.PresidioElasticsearchTemplate;
import fortscale.utils.logging.Logger;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import presidio.output.domain.records.AbstractElasticDocument;
import presidio.output.domain.records.alerts.*;
import presidio.output.domain.repositories.AlertRepository;
import presidio.output.domain.repositories.IndicatorEventRepository;
import presidio.output.domain.repositories.IndicatorRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class AlertPersistencyServiceImpl implements AlertPersistencyService {

    private static final Logger logger = Logger.getLogger(AlertPersistencyServiceImpl.class);

    @Autowired
    private AlertRepository alertRepository;

    @Autowired
    private IndicatorRepository indicatorRepository;

    @Autowired
    private IndicatorEventRepository indicatorEventRepository;

    @Autowired
    private PresidioElasticsearchTemplate elasticsearchTemplate;

    @Value("${indicators.store.page.size}")
    private int indicatorsStorePageSize;

    @Value("${events.store.page.size}")
    private int eventsStorePageSize;

    public Alert save(Alert alert) {
        save(Collections.singletonList(alert));
        return alert;
    }

    public Iterable<Alert> save(List<Alert> alerts) {
        alerts.forEach(AbstractElasticDocument::updateFieldsBeforeSave);
        // atomic save for the entire alert entities

        // save alerts
        Iterable<Alert> savedAlerts = alertRepository.saveAll(alerts);
        logger.info("{} alerts were saved", alerts.size());

        // save indicators
        List<Indicator> indicators = new ArrayList<>();
        alerts.stream()
                .filter(alert -> alert.getIndicators() != null)
                .forEach(alert -> indicators.addAll(alert.getIndicators()));
        if (CollectionUtils.isNotEmpty(indicators)) {
            //dividing indicators list to chunks-
            Iterable<List<Indicator>> indicatorsSubSets = Iterables.partition(indicators, indicatorsStorePageSize);
            indicatorsSubSets.forEach(indicatorsPartition -> indicatorRepository.saveAll(indicatorsPartition));
        }
        logger.info("{} indicators were saved", indicators.size());

        // save events
        List<IndicatorEvent> events = new ArrayList<>();
        indicators.stream()
                .filter(indicator -> indicator.getEvents() != null)
                .forEach(indicator -> events.addAll(indicator.getEvents()));
        if (CollectionUtils.isNotEmpty(events)) {
            //dividing events list to chunks-
            Iterable<List<IndicatorEvent>> eventsSubSets = Iterables.partition(events, eventsStorePageSize);
            eventsSubSets.forEach(eventsPartition -> indicatorEventRepository.saveAll(eventsPartition));
        }
        logger.info("{} events were saved", events.size());

        return savedAlerts;
    }

    public void deleteAlertAndIndicators(Alert alert) {
        // atomic delete for the entire alert entities

        // delete alert
        alertRepository.delete(alert);

        // delete indicators
        List<Indicator> indicators = indicatorRepository.removeByAlertId(alert.getId());

        // delete Indicators events
        indicators.forEach(indicator -> indicatorEventRepository.removeByIndicatorId(indicator.getId()));
    }

    @Override
    public Alert findOne(String id) {
        return alertRepository.findById(id).get();
    }

    @Override
    public Iterable<Alert> findAll() {
        return alertRepository.findAll();
    }

    @Override
    public Iterable<Alert> findAll(List<String> ids) {
        return alertRepository.findAllById(ids);
    }

    @Override
    public Page<Alert> findByEntityName(String entityName, PageRequest pageRequest) {
        return alertRepository.findByEntityName(entityName, pageRequest);
    }

    @Override
    public Page<Alert> findByEntityDocumentId(String entityDocumentId, PageRequest pageRequest) {
        return alertRepository.findByEntityDocumentId(entityDocumentId, pageRequest);
    }

    @Override
    public Page<Alert> find(AlertQuery alertQuery) {
        return alertRepository.search(new AlertElasticsearchQueryBuilder(alertQuery).build());
    }

    @Override
    public Indicator findIndicatorById(String indicatorId) {
        return indicatorRepository.findIndicatorById(indicatorId);
    }

    @Override
    public Page<Indicator> findIndicatorsByAlertId(String alertId, PageRequest pageRequest) {
        return indicatorRepository.findByAlertIdOrderByScoreContributionDesc(alertId, pageRequest);
    }

    @Override
    public Page<IndicatorEvent> findIndicatorEventsByIndicatorId(String indicatorId, PageRequest pageRequest) {
        return indicatorEventRepository.findIndicatorEventsByIndicatorIdOrderByEventTimeDesc(indicatorId, pageRequest);
    }

    @Override
    public List<Alert> removeByTimeRange(Instant startDate, Instant endDate) {
        logger.info("Going to delete alerts that were created from date {} until date {}", startDate, endDate);
        List<Alert> removedAlerts = new ArrayList<>();
        try (Stream<Alert> alerts = findAlertsByDate(startDate, endDate)) {
            alerts.forEach(alert -> {
                deleteAlertAndIndicators(alert);
                removedAlerts.add(alert);
            });
        }
        logger.info("{} alerts were deleted", removedAlerts.size());
        return removedAlerts;
    }

    @Override
    public Stream<Alert> findAlertsByDate(Instant startDate, Instant endDate) {
        if (startDate.equals(Instant.EPOCH)) {
            return alertRepository.findByEndDateLessThan(endDate.toEpochMilli());
        } else {
            return alertRepository.findByStartDateGreaterThanEqualAndEndDateLessThanEqual(startDate.toEpochMilli(), endDate.toEpochMilli());
        }
    }

    @Override
    public Stream<Indicator> findIndicatorByDate(Instant startDate, Instant endDate) {
        return indicatorRepository.findByStartDateGreaterThanEqualAndEndDateLessThanEqual(startDate.toEpochMilli(), endDate.toEpochMilli());
    }

    @Override
    public List<Alert> findByEntityDocumentId(String entityDocumentId) {
        List<Alert> alerts;
        try (Stream<Alert> stream = alertRepository.findByEntityDocumentId(entityDocumentId)) {
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