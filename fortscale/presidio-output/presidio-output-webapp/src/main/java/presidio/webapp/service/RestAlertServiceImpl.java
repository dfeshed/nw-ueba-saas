package presidio.webapp.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import presidio.output.domain.records.alerts.AlertQuery;
import presidio.output.domain.services.alerts.AlertPersistencyService;
import presidio.webapp.dto.Alert;
import presidio.webapp.model.AlertSeverity;

import java.math.BigDecimal;
import java.util.*;

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
    public presidio.webapp.model.Alert getAlertById(String id) {
        presidio.output.domain.records.alerts.Alert alertData = elasticAlertService.findOne(id);
        presidio.webapp.model.Alert resultAlert = null;
        if (alertData != null) {
            resultAlert = createRestAlert(alertData);
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
        alerts.forEach(alert -> restAlerts.add(createRestAlert(alert)));
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
        if (BooleanUtils.isTrue(alertQuery.getAggregateBySeverity())) {
            alertQueryBuilder.aggregateBySeverity(alertQuery.getAggregateBySeverity());
        }
        AlertQuery convertedAlertQuery = alertQueryBuilder.build();
        return convertedAlertQuery;
    }

    @Override
    public List<presidio.webapp.model.Alert> getAlertsByUserId(String userId) {
        Page<presidio.output.domain.records.alerts.Alert> alerts = elasticAlertService.findByUserId(userId, new PageRequest(pageNumber, pageSize));
        if (alerts.hasContent()) {
            List restAlerts = new ArrayList();
            alerts.forEach(alert -> restAlerts.add(createRestAlert(alert)));
            return restAlerts;
        }
        return null;
    }

    @Override
    public Map<String, List<presidio.webapp.model.Alert>> getAlertsByUsersIds(Collection<String> userIds) {
        Page<presidio.output.domain.records.alerts.Alert> alerts = elasticAlertService.findByUserIdIn(userIds, new PageRequest(pageNumber, pageSize));
        if (alerts.hasContent()) {
            List restAlerts = new ArrayList();
            alerts.forEach(alert -> restAlerts.add(createRestAlert(alert)));
            return userIdsToAlerts(restAlerts, (List) userIds);
        }
        return null;
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
