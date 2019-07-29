package presidio.webapp.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.elasticsearch.search.aggregations.Aggregation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.stereotype.Service;
import presidio.output.domain.records.alerts.AlertEnums;
import presidio.output.domain.records.alerts.AlertQuery;
import presidio.output.domain.records.alerts.Bucket;
import presidio.output.domain.records.alerts.CountAggregation;
import presidio.output.domain.records.alerts.IndicatorEvent;
import presidio.output.domain.records.alerts.TimeAggregation;
import presidio.output.domain.records.alerts.WeekdayAggregation;
import presidio.output.domain.services.alerts.AlertPersistencyService;
import presidio.webapp.model.*;
import presidio.webapp.model.AlertQueryEnums.AlertSeverity;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class RestAlertServiceImpl implements RestAlertService {


    private final FeedbackService feedbackService;
    private final AlertPersistencyService alertPersistencyService;
    private final int pageNumber;
    private final int pageSize;

    public RestAlertServiceImpl(AlertPersistencyService alertPersistencyService,
                                FeedbackService feedbackService,
                                int pageNumber,
                                int pageSize) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.alertPersistencyService = alertPersistencyService;
        this.feedbackService = feedbackService;
    }

    @Override
    public presidio.webapp.model.Alert getAlertById(String id, boolean expand) {
        Optional<presidio.output.domain.records.alerts.Alert> optionalAlertData = alertPersistencyService.findOne(id);
        presidio.webapp.model.Alert resultAlert = null;
        if (optionalAlertData.isPresent()) {
            resultAlert = createRestAlert(optionalAlertData.get());
            if (expand) {
                List<Indicator> restIndicators = new ArrayList<>();
                Page<presidio.output.domain.records.alerts.Indicator> indicators = alertPersistencyService.findIndicatorsByAlertId(id, new PageRequest(pageNumber, pageSize));
                for (presidio.output.domain.records.alerts.Indicator indicator : indicators) {
                    // workaround - projection doesn't work
                    indicator.setHistoricalData(null);
                    restIndicators.add(createRestIndicator(indicator));
                }
                resultAlert.setIndicators(restIndicators);
            }
        }
        return resultAlert;
    }

    @Override
    public AlertsWrapper getAlerts(presidio.webapp.model.AlertQuery alertQuery) {
        AlertQuery convertedAlertQuery = createQuery(alertQuery);
        Page<presidio.output.domain.records.alerts.Alert> alerts;
        try {
            alerts = alertPersistencyService.find(convertedAlertQuery);
        } catch (Exception ex) {
            alerts = new PageImpl<>(null, null, 0);
        }
        List<presidio.webapp.model.Alert> restAlerts = new ArrayList<>();
        int totalElements = Math.toIntExact(alerts.getTotalElements());
        Map<String, Aggregation> alertAggregations = null;
        if (alerts.getTotalElements() > 0) {
            for (presidio.output.domain.records.alerts.Alert alert : alerts) {
                presidio.webapp.model.Alert restAlert = createRestAlert(alert);
                if (alertQuery.getExpand()) {
                    // TODO: improve performance with in query
                    List<Indicator> restIndicators = new ArrayList<>();
                    Page<presidio.output.domain.records.alerts.Indicator> indicators = alertPersistencyService.findIndicatorsByAlertId(alert.getId(), new PageRequest(pageNumber, pageSize));
                    for (presidio.output.domain.records.alerts.Indicator indicator : indicators) {
                        indicator.setHistoricalData(null);
                        restIndicators.add(createRestIndicator(indicator));
                    }
                    restAlert.setIndicators(restIndicators);
                }
                restAlerts.add(restAlert);
            }

            if (CollectionUtils.isNotEmpty(alertQuery.getAggregateBy())) {
                alertAggregations = ((AggregatedPageImpl<presidio.output.domain.records.alerts.Alert>) alerts).getAggregations().asMap();
            }
        }

        return createAlertsWrapper(restAlerts, totalElements, alertQuery.getPageNumber(), alertAggregations);
    }

    private AlertsWrapper createAlertsWrapper(List restAlerts, int totalNumberOfElements, Integer pageNumber, Map<String, Aggregation> alertAggregations) {
        AlertsWrapper alertsWrapper = new AlertsWrapper();
        alertsWrapper.setTotal(totalNumberOfElements);
        alertsWrapper.setPage(pageNumber);
        if (CollectionUtils.isNotEmpty(restAlerts)) {
            alertsWrapper.setAlerts(restAlerts);

            if (MapUtils.isNotEmpty(alertAggregations)) {
                Map<String, String> aggregationNamesEnumMapping = new HashMap<>();
                alertAggregations.keySet().forEach(aggregationName -> aggregationNamesEnumMapping.put(aggregationName, AlertQueryEnums.AlertQueryAggregationFieldName.fromValue(aggregationName).name()));
                Map<String, Map<String, Long>> aggregations = RestUtils.convertAggregationsToMap(alertAggregations, aggregationNamesEnumMapping);
                alertsWrapper.setAggregationData(aggregations);
            }
        } else {
            alertsWrapper.setAlerts(new ArrayList());
        }
        return alertsWrapper;
    }

    private IndicatorsWrapper createIndicatorsWrapper(List restIndicators, int totalNumberOfElements, Integer pageNumber) {
        IndicatorsWrapper indicatorsWrapper = new IndicatorsWrapper();
        if (CollectionUtils.isNotEmpty(restIndicators)) {
            indicatorsWrapper.setIndicators(restIndicators);
            indicatorsWrapper.setTotal(totalNumberOfElements);
            if (pageNumber != null) {
                indicatorsWrapper.setPage(pageNumber);
            }
            indicatorsWrapper.setPage(pageNumber);
        } else {
            indicatorsWrapper.setIndicators(new ArrayList());
            indicatorsWrapper.setTotal(0);
            indicatorsWrapper.setPage(0);
        }
        return indicatorsWrapper;
    }

    private EventsWrapper createEventsWrapper(List restEvents, int totalNumberOfElements, Integer pageNumber) {
        EventsWrapper eventsWrapper = new EventsWrapper();
        if (CollectionUtils.isNotEmpty(restEvents)) {
            eventsWrapper.setEvents(restEvents);
            eventsWrapper.setTotal(totalNumberOfElements);
            if (pageNumber != null) {
                eventsWrapper.setPage(pageNumber);
            }
            eventsWrapper.setPage(pageNumber);

        } else {
            eventsWrapper.setEvents(new ArrayList());
            eventsWrapper.setTotal(0);
            eventsWrapper.setPage(0);
        }
        return eventsWrapper;
    }

    private AlertQuery createQuery(presidio.webapp.model.AlertQuery alertQuery) {
        AlertQuery.AlertQueryBuilder alertQueryBuilder = new AlertQuery.AlertQueryBuilder();
        if (CollectionUtils.isNotEmpty(alertQuery.getEntityDocumentIds())) {
            alertQueryBuilder.filterByEntityDocumentId(alertQuery.getEntityDocumentIds());
        }
        if (CollectionUtils.isNotEmpty(alertQuery.getEntityNames())) {
            alertQueryBuilder.filterByEntityName(alertQuery.getEntityNames());
        }
        if (alertQuery.getPageSize() != null) {
            alertQueryBuilder.setPageSize(alertQuery.getPageSize());
        }
        if (alertQuery.getPageNumber() != null) {
            alertQueryBuilder.setPageNumber(alertQuery.getPageNumber());
        }
        if (CollectionUtils.isNotEmpty(alertQuery.getClassification())) {
            alertQueryBuilder.filterByClassification(alertQuery.getClassification());
        }
        if (alertQuery.getMaxScore() != null) {
            alertQueryBuilder.filterByMaxScore(alertQuery.getMaxScore());
        }
        if (alertQuery.getMinScore() != null) {
            alertQueryBuilder.filterByMinScore(alertQuery.getMinScore());
        }
        if (CollectionUtils.isNotEmpty(alertQuery.getTags())) {
            alertQueryBuilder.filterByTags(alertQuery.getTags());
        }
        if (CollectionUtils.isNotEmpty(alertQuery.getIndicatorsName())) {
            alertQueryBuilder.filterByIndicatorNames(alertQuery.getIndicatorsName());
        }
        if (alertQuery.getStartTimeFrom() != null) {
            alertQueryBuilder.filterByStartDate(alertQuery.getStartTimeFrom().longValue());
        }
        if (alertQuery.getStartTimeTo() != null) {
            alertQueryBuilder.filterByEndDate(alertQuery.getStartTimeTo().longValue());
        }
        if (CollectionUtils.isNotEmpty(alertQuery.getSeverity())) {
            List<String> severity = new ArrayList<>();
            alertQuery.getSeverity().forEach(severityParam -> severity.add(severityParam.toString()));
            alertQueryBuilder.filterBySeverity(severity);
        }
        if (CollectionUtils.isNotEmpty(alertQuery.getFeedback())) {
            List<String> feedback = new ArrayList<>();
            alertQuery.getFeedback().forEach(feedbackParam -> feedback.add(feedbackParam.toString()));
            alertQueryBuilder.filterByFeedback(feedback);
        }
        if (CollectionUtils.isNotEmpty(alertQuery.getSortFieldNames()) && alertQuery.getSortDirection() != null) {
            List<Sort.Order> orders = new ArrayList<>();
            alertQuery.getSortFieldNames().forEach(s -> orders.add(new Sort.Order(alertQuery.getSortDirection(), s.toString())));
            alertQueryBuilder.sortField(new Sort(orders));
        }
        if (CollectionUtils.isNotEmpty(alertQuery.getAggregateBy())) {
            List<String> aggregateByFields = new ArrayList<>();
            alertQuery.getAggregateBy().forEach(alertQueryAggregationFieldName -> aggregateByFields.add(alertQueryAggregationFieldName.toString()));
            alertQueryBuilder.aggregateByFields(aggregateByFields);
        }
        return alertQueryBuilder.build();
    }

    @Override
    public AlertsWrapper getAlertsByEntityDocumentId(String entityDocumentId, boolean expand) {
        Page<presidio.output.domain.records.alerts.Alert> alerts;
        alerts = alertPersistencyService.findByEntityDocumentId(entityDocumentId, new PageRequest(pageNumber, pageSize));
        List restAlerts = new ArrayList();
        int totalElements = 0;
        if (alerts.getTotalElements() > 0) {
            for (presidio.output.domain.records.alerts.Alert alert : alerts) {
                presidio.webapp.model.Alert restAlert = createRestAlert(alert);
                if (expand) {
                    List<Indicator> restIndicators = new ArrayList<Indicator>();
                    Page<presidio.output.domain.records.alerts.Indicator> indicators = alertPersistencyService.findIndicatorsByAlertId(alert.getId(), new PageRequest(pageNumber, pageSize));
                    for (presidio.output.domain.records.alerts.Indicator indicator : indicators) {
                        // workaround - projection doesn't work
                        indicator.setHistoricalData(null);
                        restIndicators.add(createRestIndicator(indicator));
                    }
                    restAlert.setIndicators(restIndicators);
                }
                restAlerts.add(restAlert);
            }
            totalElements = Math.toIntExact(alerts.getTotalElements());
        }
        return createAlertsWrapper(restAlerts, totalElements, 0, null);
    }

    @Override
    public Map<String, List<Alert>> getAlertsByEntityDocumentIds(Collection<String> entityDocumentIds) {
        Map<String, List<Alert>> alertsByEntityIds = new HashMap<>();
        PageRequest pageRequest = new PageRequest(pageNumber, pageSize);
        entityDocumentIds.forEach(id -> {
            Page<presidio.output.domain.records.alerts.Alert> alertsByEntityId = alertPersistencyService.findByEntityDocumentId(id, pageRequest);
            alertsByEntityIds.put(id, convertToRestAlerts(alertsByEntityId));
        });
        return alertsByEntityIds;
    }

    private List<Alert> convertToRestAlerts(Page<presidio.output.domain.records.alerts.Alert> alertsByEntityId) {
        List<Alert> restAlerts = new ArrayList<>();
        alertsByEntityId.forEach(alert -> restAlerts.add(createRestAlert(alert)));
        return restAlerts;
    }

    @Override
    public Indicator getIndicatorById(String indicatorId, boolean expand) {
        presidio.webapp.model.Indicator restIndicator;
        if (expand) {
            presidio.output.domain.records.alerts.Indicator indicator = alertPersistencyService.findIndicatorById(indicatorId);
            restIndicator = createRestIndicator(indicator);
        } else {
            // workaround - projection doesn't work
            presidio.output.domain.records.alerts.Indicator indicator = alertPersistencyService.findIndicatorById(indicatorId);
            indicator.setHistoricalData(null);
            restIndicator = createRestIndicator(indicator);
        }
        return restIndicator;
    }

    @Override
    public IndicatorsWrapper getIndicatorsByAlertId(String alertId, IndicatorQuery indicatorQuery) {
        List<Indicator> restIndicators = new ArrayList<>();
        int totalElements = 0;
        int pageNumber = indicatorQuery.getPageNumber() != null ? indicatorQuery.getPageNumber() : 0;
        int pageSize = indicatorQuery.getPageSize() != null ? indicatorQuery.getPageSize() : 10;
        PageRequest pageRequest = new PageRequest(pageNumber, pageSize);
        if (Boolean.TRUE.equals(indicatorQuery.getExpand())) {
            Page<presidio.output.domain.records.alerts.Indicator> indicators = alertPersistencyService.findIndicatorsByAlertId(alertId, pageRequest);
            for (presidio.output.domain.records.alerts.Indicator indicator : indicators) {
                restIndicators.add(createRestIndicator(indicator));
            }
            totalElements = Math.toIntExact(indicators.getTotalElements());
        } else {
            // workaround - projection doesn't work
            Page<presidio.output.domain.records.alerts.Indicator> indicators = alertPersistencyService.findIndicatorsByAlertId(alertId, pageRequest);

            for (presidio.output.domain.records.alerts.Indicator indicator : indicators) {
                indicator.setHistoricalData(null);
                restIndicators.add(createRestIndicator(indicator));
            }
            totalElements = Math.toIntExact(indicators.getTotalElements());
        }
        return createIndicatorsWrapper(restIndicators, totalElements, pageRequest.getPageNumber());
    }

    @Override
    public EventsWrapper getIndicatorEventsByIndicatorId(String indicatorId, EventQuery eventQuery) {
        List<presidio.webapp.model.Event> restEvents = new ArrayList<>();
        int pageNumber = eventQuery.getPageNumber() != null ? eventQuery.getPageNumber() : 0;
        int pageSize = eventQuery.getPageSize() != null ? eventQuery.getPageSize() : 10;
        PageRequest pageRequest = new PageRequest(pageNumber, pageSize);
        Page<IndicatorEvent> events = alertPersistencyService.findIndicatorEventsByIndicatorId(indicatorId, pageRequest);
        for (presidio.output.domain.records.alerts.IndicatorEvent event : events) {
            restEvents.add(createRestEvent(event));
        }
        int totalElements = Math.toIntExact(events.getTotalElements());
        return createEventsWrapper(restEvents, totalElements, eventQuery.getPageNumber());
    }

    @Override
    public void updateAlertFeedback(List<String> alertIds, AlertQueryEnums.AlertFeedback feedback) {
        feedbackService.updateAlertFeedback(alertIds, AlertEnums.AlertFeedback.valueOf(feedback.toString()));
    }

    private presidio.webapp.model.Alert createRestAlert(presidio.output.domain.records.alerts.Alert alert) {
        presidio.webapp.model.Alert restAlert = new presidio.webapp.model.Alert();
        restAlert.setScore(Double.valueOf(alert.getScore()).intValue());
        restAlert.setEndDate(BigDecimal.valueOf(alert.getEndDate().getTime()));
        restAlert.setStartDate(BigDecimal.valueOf(alert.getStartDate().getTime()));
        restAlert.setId(alert.getId());
        restAlert.setClassifiation(alert.getClassifications());
        restAlert.setEntityName(alert.getEntityName());
        restAlert.setEntityDocumentId(alert.getEntityDocumentId());
        restAlert.setSeverity(AlertSeverity.fromValue(alert.getSeverity().toString()));
        restAlert.setIndicatorsNum(alert.getIndicatorsNum());
        restAlert.setIndicatorsName(alert.getIndicatorsNames());
        restAlert.setTimeframe(Alert.TimeframeEnum.fromValue(alert.getTimeframe().toString()));
        restAlert.setEntityScoreContribution(new BigDecimal(alert.getContributionToEntityScore()));
        restAlert.setFeedback(AlertQueryEnums.AlertFeedback.fromValue(alert.getFeedback().toString()));
        return restAlert;
    }

    private presidio.webapp.model.Indicator createRestIndicator(presidio.output.domain.records.alerts.Indicator indicator) {
        presidio.webapp.model.Indicator restIndicator = new presidio.webapp.model.Indicator();
        restIndicator.setId(indicator.getId());
        restIndicator.setName(indicator.getName());
        restIndicator.setAnomalyValue(indicator.getAnomalyValue());
        restIndicator.setStartDate(BigDecimal.valueOf(TimeUnit.SECONDS.convert(indicator.getStartDate().getTime(), TimeUnit.MILLISECONDS)));
        restIndicator.setEndDate(BigDecimal.valueOf(TimeUnit.SECONDS.convert(indicator.getEndDate().getTime(), TimeUnit.MILLISECONDS)));
        restIndicator.setSchema(indicator.getSchema().name());
        restIndicator.setScore(indicator.getScore());
        restIndicator.setEventsNum(indicator.getEventsNum());
        restIndicator.setScoreContribution(indicator.getScoreContribution());
        restIndicator.setType(Indicator.TypeEnum.fromValue(indicator.getType().name()));
        restIndicator.setContexts(indicator.getContexts());
        if (indicator.getHistoricalData() != null) {
            restIndicator.setHistoricalData(createRestHistorical(indicator.getHistoricalData()));
        }
        return restIndicator;
    }

    private presidio.webapp.model.Event createRestEvent(presidio.output.domain.records.alerts.IndicatorEvent indicatorEvent) {
        presidio.webapp.model.Event restEvent = new presidio.webapp.model.Event();
        restEvent.setId(indicatorEvent.getId());
        restEvent.setSchema(indicatorEvent.getSchema().name());
        restEvent.setTime(BigDecimal.valueOf(TimeUnit.SECONDS.convert(indicatorEvent.getEventTime().getTime(), TimeUnit.MILLISECONDS)));
        restEvent.putAll(indicatorEvent.getFeatures());
        if (MapUtils.isNotEmpty(indicatorEvent.getScores())) {
            restEvent.setScores(indicatorEvent.getScores());
            restEvent.put("scores", indicatorEvent.getScores());
        }
        return restEvent;
    }

    private presidio.webapp.model.HistoricalData createRestHistorical(presidio.output.domain.records.alerts.HistoricalData historicalData) {

        presidio.webapp.model.HistoricalData restHistoricalData = null;

        if (historicalData.getAggregation() instanceof CountAggregation) {

            CountAggregation aggr = (CountAggregation) historicalData.getAggregation();
            List<Bucket<String, Double>> buckets = aggr.getBuckets();
            restHistoricalData = new HistoricalDataCountAggregation();
            CountBuckets restBuckets = new CountBuckets();
            for (Bucket<String, Double> bucket : buckets) {
                CountBucket restBucket = new CountBucket();
                restBucket.setKey(bucket.getKey());
                restBucket.setValue(bucket.getValue().intValue());
                restBucket.setAnomaly(bucket.isAnomaly());
                restBuckets.add(restBucket);
            }
            ((HistoricalDataCountAggregation) restHistoricalData).setType(HistoricalDataCountAggregation.TypeEnum.CountAggregation);
            ((HistoricalDataCountAggregation) restHistoricalData).setBuckets(restBuckets);

        }

        if (historicalData.getAggregation() instanceof TimeAggregation) {

            TimeAggregation aggr = (TimeAggregation) historicalData.getAggregation();
            List<Bucket<String, Double>> buckets = aggr.getBuckets();


            restHistoricalData = new HistoricalDataTimeAggregation();
            TimeBuckets restBuckets = new TimeBuckets();

            for (Bucket<String, Double> bucket : buckets) {

                TimeBucket restBucket = new TimeBucket();
                BigDecimal time = BigDecimal.valueOf(Long.parseLong(bucket.getKey()));
                restBucket.setKey(time);
                restBucket.setValue(bucket.getValue());
                restBucket.setAnomaly(bucket.isAnomaly());
                restBuckets.add(restBucket);
            }
            ((HistoricalDataTimeAggregation) restHistoricalData).setType(HistoricalDataTimeAggregation.TypeEnum.TimeAggregation);
            ((HistoricalDataTimeAggregation) restHistoricalData).setBuckets(restBuckets);

        }


        if (historicalData.getAggregation() instanceof WeekdayAggregation) {

            WeekdayAggregation aggr = (WeekdayAggregation) historicalData.getAggregation();

            restHistoricalData = new HistoricalDataWeekdayAggregation();

            List<Bucket<String, List<Bucket<String, Integer>>>> dailyBuckets = aggr.getBuckets();
            DailyBuckets restDailyBuckets = new DailyBuckets();

            // for each day of week
            for (Bucket<String, List<Bucket<String, Integer>>> dailyBucket : dailyBuckets) {

                DailyBucket restDailyBucket = new DailyBucket();
                restDailyBucket.setKey(dailyBucket.getKey());
                List<Bucket<String, Integer>> hourlyBuckets = dailyBucket.getValue();

                // add hour of day
                HourlyBuckets restHourlyBuckets = new HourlyBuckets();
                for (Bucket<String, Integer> hourlyBucket : hourlyBuckets) {
                    HourlyBucket restHourlyBucket = new HourlyBucket();
                    restHourlyBucket.setKey(hourlyBucket.getKey());
                    restHourlyBucket.setValue(hourlyBucket.getValue());
                    restHourlyBucket.setAnomaly(hourlyBucket.isAnomaly());
                    restHourlyBuckets.add(restHourlyBucket);
                }

                restDailyBucket.setValue(restHourlyBuckets);
                restDailyBuckets.add(restDailyBucket);

            }
            ((HistoricalDataWeekdayAggregation) restHistoricalData).setType(HistoricalDataWeekdayAggregation.TypeEnum.WeekdayAggregation);
            ((HistoricalDataWeekdayAggregation) restHistoricalData).setBuckets(restDailyBuckets);

        }
        return restHistoricalData;
    }
}
