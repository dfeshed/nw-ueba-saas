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
import presidio.output.domain.records.alerts.AlertQuery;
import presidio.output.domain.services.alerts.AlertPersistencyService;
import presidio.webapp.dto.Alert;
import presidio.webapp.model.AlertSeverity;
import presidio.webapp.model.AlertsWrapper;
import presidio.webapp.model.Indicator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RestAlertServiceImpl implements RestAlertService {


    private final AlertPersistencyService elasticAlertService;
    private final int pageNumber;
    private final int pageSize;

    public RestAlertServiceImpl(AlertPersistencyService elasticAlertService, int pageNumber, int pageSize) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.elasticAlertService = elasticAlertService;
    }

    @Override
    public presidio.webapp.model.Alert getAlertById(String id, boolean expand) {
        presidio.output.domain.records.alerts.Alert alertData = elasticAlertService.findOne(id);
        presidio.webapp.model.Alert resultAlert = null;
        if (alertData != null) {
            resultAlert = createRestAlert(alertData);
        }
        if (expand) {
            List<Indicator> indicator = MockUtils.mockIndicators(false);
            resultAlert.setIndicators(indicator);
        }
        return null;
    }

    @Override
    public AlertsWrapper getAlerts(presidio.webapp.model.AlertQuery alertQuery) {
        AlertQuery convertedAlertQuery = createQuery(alertQuery);
        Page<presidio.output.domain.records.alerts.Alert> alerts;
        try {
            alerts = elasticAlertService.find(convertedAlertQuery);
        } catch (Exception ex) {
            alerts = new PageImpl<>(null, null, 0);
        }
        List<presidio.webapp.model.Alert> restAlerts = new ArrayList<>();
        int totalElements = 0;
        Map<String, Aggregation> alertAggregations = null;
        if (alerts.getTotalElements() > 0) {
            for (presidio.output.domain.records.alerts.Alert alert : alerts) {
                presidio.webapp.model.Alert restAlert = createRestAlert(alert);
                if (alertQuery.getExpand().booleanValue()) {
                    restAlert.setIndicators(MockUtils.mockIndicators(false));
                }
                restAlerts.add(restAlert);
            }
            totalElements = Math.toIntExact(alerts.getTotalElements());
            if (CollectionUtils.isNotEmpty(alertQuery.getAggregateBy())) {
                alertAggregations = ((AggregatedPageImpl<presidio.output.domain.records.alerts.Alert>) alerts).getAggregations().asMap();
            }
        }

        return createAlertsWrapper(restAlerts, totalElements, alertQuery.getPageNumber(), alertAggregations);
    }

    private AlertsWrapper createAlertsWrapper(List restAlerts, int totalNumberOfElements, Integer pageNumber, Map<String, Aggregation> alertAggregations) {
        AlertsWrapper alertsWrapper = new AlertsWrapper();
        if (CollectionUtils.isNotEmpty(restAlerts)) {
            alertsWrapper.setAlerts(restAlerts);
            alertsWrapper.setTotal(totalNumberOfElements);
            if (pageNumber != null) {
                alertsWrapper.setPage(pageNumber);
            }
            alertsWrapper.setPage(pageNumber);

            if (MapUtils.isNotEmpty(alertAggregations)) {
                Map<String, Map<String, Long>> aggregations = RestUtils.convertAggregationsToMap(alertAggregations);
                alertsWrapper.setAggregationData(aggregations);
            }
        } else {
            alertsWrapper.setAlerts(new ArrayList());
            alertsWrapper.setTotal(0);
            alertsWrapper.setPage(0);
        }
        return alertsWrapper;
    }

    private AlertQuery createQuery(presidio.webapp.model.AlertQuery alertQuery) {
        AlertQuery.AlertQueryBuilder alertQueryBuilder = new AlertQuery.AlertQueryBuilder();
        if (CollectionUtils.isNotEmpty(alertQuery.getUsersId())) {
            alertQueryBuilder.filterByUserId(alertQuery.getUsersId());
        }
        if (CollectionUtils.isNotEmpty(alertQuery.getUserName())) {
            alertQueryBuilder.filterByUserName(alertQuery.getUserName());
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
        if (CollectionUtils.isNotEmpty(alertQuery.getUsersId())) {
            alertQueryBuilder.filterByUserName(alertQuery.getUsersId());
        }
        if (alertQuery.getStartTimeFrom() != null) {
            alertQueryBuilder.filterByStartDate(alertQuery.getStartTimeFrom().longValue());
        }
        if (alertQuery.getStartTimeTo() != null) {
            alertQueryBuilder.filterByEndDate(alertQuery.getStartTimeTo().longValue());
        }
        if (CollectionUtils.isNotEmpty(alertQuery.getSeverity())) {
            List<String> severity = new ArrayList<>();
            alertQuery.getSeverity().forEach(severityParam -> {
                severity.add(severityParam.toString());
            });
            alertQueryBuilder.filterBySeverity(severity);
        }
        if (CollectionUtils.isNotEmpty(alertQuery.getFeedback())) {
            List<String> feedback = new ArrayList<>();
            alertQuery.getFeedback().forEach(feedbackParam -> {
                feedback.add(feedbackParam.toString());
            });
            alertQueryBuilder.filterByFeedback(feedback);
        }
        if (CollectionUtils.isNotEmpty(alertQuery.getSortFieldNames()) && alertQuery.getSortDirection() != null) {
            List<Sort.Order> orders = new ArrayList<>();
            alertQuery.getSortFieldNames().forEach(s -> {
                orders.add(new Sort.Order(alertQuery.getSortDirection(), s.toString()));
            });
            alertQueryBuilder.sortField(new Sort(orders));
        }
        if (CollectionUtils.isNotEmpty(alertQuery.getAggregateBy())) {
            List<String> aggregateByFields = new ArrayList<>();
            alertQuery.getAggregateBy().forEach(alertQueryAggregationFieldName -> {
                aggregateByFields.add(alertQueryAggregationFieldName.toString());
            });
            alertQueryBuilder.aggregateByFields(aggregateByFields);
        }
        AlertQuery convertedAlertQuery = alertQueryBuilder.build();
        return convertedAlertQuery;
    }

    @Override
    public AlertsWrapper getAlertsByUserId(String userId, boolean expand) {
        Page<presidio.output.domain.records.alerts.Alert> alerts;
        alerts = elasticAlertService.findByUserId(userId, new PageRequest(pageNumber, pageSize));
        List restAlerts = new ArrayList();
        int totalElements = 0;
        if (alerts.getTotalElements() > 0) {
            for (presidio.output.domain.records.alerts.Alert alert : alerts) {
                presidio.webapp.model.Alert restAlert = createRestAlert(alert);
                if (expand) {
                    restAlert.setIndicators(MockUtils.mockIndicators(false));
                }
                restAlerts.add(restAlert);
            }
            totalElements = Math.toIntExact(alerts.getTotalElements());
        }
        return createAlertsWrapper(restAlerts, totalElements, 0, null);
    }

    @Override
    public Map<String, List<presidio.webapp.model.Alert>> getAlertsByUsersIds(Collection<String> userIds) {
        Page<presidio.output.domain.records.alerts.Alert> alerts;
        try {
            alerts = elasticAlertService.findByUserIdIn(userIds, new PageRequest(pageNumber, pageSize));
        } catch (Exception ex) {
            alerts = new PageImpl<>(null, null, 0);
        }
        List restAlerts;
        if (alerts.getTotalElements() > 0) {
            restAlerts = new ArrayList();
            alerts.forEach(alert -> restAlerts.add(createRestAlert(alert)));
            return userIdsToAlerts(restAlerts, (List) userIds);
        }
        return null;
    }

    @Override
    public Indicator getIndicatorById(String indicatorId, boolean expand) {
        // TEMPORARY CODE - DO NOT REVIEW
        Indicator restIndicator = MockUtils.mockIndicator(indicatorId, expand);
        return restIndicator;
    }

    @Override
    public List<presidio.webapp.model.Indicator> getIndicatorsByAlertId(String alertId, presidio.webapp.model.IndicatorQuery indicatorQuery) {
        // TEMPORARY CODE - DO NOT REVIEW
        List<Indicator> restIndicators = MockUtils.mockIndicators(indicatorQuery.getExpand().booleanValue());
        return restIndicators;
    }

    @Override
    public List<Event> getIndicatorEventsByIndicatorId(String indicatorId, EventQuery eventQuery) {
        // TEMPORARY CODE - DO NOT REVIEW
        List<Event> restEvents = MockUtils.mockEvents();
        return restEvents;
    }

    private presidio.webapp.model.Alert createRestAlert(presidio.output.domain.records.alerts.Alert alert) {
        presidio.webapp.model.Alert restAlert = new presidio.webapp.model.Alert();
        restAlert.setScore(Double.valueOf(alert.getScore()).intValue());
        restAlert.setEndDate(BigDecimal.valueOf(alert.getEndDate()));
        restAlert.setStartDate(BigDecimal.valueOf(alert.getStartDate()));
        restAlert.setId(alert.getId());
        restAlert.setClassifiation(alert.getClassifications());
        restAlert.setUsername(alert.getUserName());
        restAlert.setUserId(alert.getUserId());
        restAlert.setSeverity(AlertSeverity.fromValue(alert.getSeverity().toString()));
        restAlert.setIndicatorsNum(alert.getIndicatorsNum());
        restAlert.setTimeframe(presidio.webapp.model.Alert.TimeframeEnum.fromValue(alert.getTimeframe().toString()));
        return restAlert;
    }


    private Map<String, List<presidio.webapp.model.Alert>> userIdsToAlerts(List<presidio.webapp.model.Alert> alerts, List<String> usersIds) {
        Map<String, List<presidio.webapp.model.Alert>> usersIdsToAlertsMap = new HashMap<>();
        List<presidio.webapp.model.Alert> tempAlerts;
        for (String id : usersIds) {
            tempAlerts = null;
            for (presidio.webapp.model.Alert alert : alerts) {
                if (alert.getUserId().equals(id)) {
                    if (tempAlerts == null)
                        tempAlerts = new ArrayList<>();
                    tempAlerts.add(alert);
                }
            }
            usersIdsToAlertsMap.put(id, tempAlerts);
        }
        return usersIdsToAlertsMap;
    }
}
