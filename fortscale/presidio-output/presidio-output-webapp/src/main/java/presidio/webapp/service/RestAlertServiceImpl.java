package presidio.webapp.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections.CollectionUtils;
import org.elasticsearch.rest.RestUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import presidio.output.domain.records.alerts.AlertQuery;
import presidio.output.domain.services.alerts.AlertPersistencyService;
import presidio.webapp.dto.Alert;
import presidio.webapp.model.AlertSeverity;
import presidio.webapp.model.Event;
import presidio.webapp.model.EventQuery;
import presidio.webapp.model.Indicator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

        return resultAlert;
    }

    @Override
    public Alert createResult(presidio.output.domain.records.alerts.Alert alertData) {
        Alert resultAlert = new Alert();
        resultAlert.setId(alertData.getId());
        resultAlert.setUsername(alertData.getUserName());
        resultAlert.setIndicatorsNum(alertData.getIndicatorsNum());
        resultAlert.setStartDate(alertData.getStartDate());
        resultAlert.setEndDate(alertData.getEndDate());
        resultAlert.setScore(alertData.getScore());
        resultAlert.setClassifications(alertData.getClassifications());
        return resultAlert;
    }

    @Override
    public List<presidio.webapp.model.Alert> getAlerts(presidio.webapp.model.AlertQuery alertQuery) {
        AlertQuery convertedAlertQuery = createQuery(alertQuery);
        Page<presidio.output.domain.records.alerts.Alert> alerts = elasticAlertService.find(convertedAlertQuery);
        List restAlerts = new ArrayList();
        for (presidio.output.domain.records.alerts.Alert alert:alerts) {
            presidio.webapp.model.Alert restAlert = createRestAlert(alert);
            if (alertQuery.getExpand().booleanValue()) {
                restAlert.setIndicators(MockUtils.mockIndicators(false));
            }
            restAlerts.add(restAlert);
        }
        return restAlerts;
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
            alertQuery.getSort().forEach(feedbackParam -> {
                feedback.add(feedbackParam.toString());
            });
            alertQueryBuilder.filterByFeedback(feedback);
        }
        if (CollectionUtils.isNotEmpty(alertQuery.getSort())) {
            List<Sort.Order> orders = new ArrayList<>();
            alertQuery.getSort().forEach(s -> {
                Sort.Direction direction = Sort.Direction.fromString(s.getDirection().name());
                orders.add(new Sort.Order(direction, s.getFieldNames().name()));

            });
            alertQueryBuilder.sortField(new Sort(orders));
        }
        AlertQuery convertedAlertQuery = alertQueryBuilder.build();
        return convertedAlertQuery;
    }

    @Override
    public List<presidio.webapp.model.Alert> getAlertsByUserId(String userId, boolean expand) {
        Page<presidio.output.domain.records.alerts.Alert> alerts = elasticAlertService.findByUserId(userId, new PageRequest(pageNumber, pageSize));
        if (alerts.hasContent()) {
            List restAlerts = new ArrayList();
            for (presidio.output.domain.records.alerts.Alert alert : alerts) {
                presidio.webapp.model.Alert restAlert = createRestAlert(alert);
                if (expand) {
                    restAlert.setIndicators(MockUtils.mockIndicators(false));
                }
                restAlerts.add(restAlert);
            }
            return restAlerts;
        }

        return null;
    }

    @Override
    public List<presidio.webapp.model.Alert> getAlertsByUsersIds(Collection<String> userId) {
        Page<presidio.output.domain.records.alerts.Alert> alerts = elasticAlertService.findByUserIdIn(userId, new PageRequest(pageNumber, pageSize));
        if (alerts.hasContent()) {
            List restAlerts = new ArrayList();
            alerts.forEach(alert -> restAlerts.add(createRestAlert(alert)));
            return restAlerts;
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



}
