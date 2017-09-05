package presidio.webapp.service;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import presidio.output.domain.records.alerts.AlertQuery;
import presidio.output.domain.services.alerts.AlertPersistencyService;
import presidio.webapp.dto.Alert;

import java.util.ArrayList;
import java.util.List;

@Service
public class RestAlertServiceImpl implements RestAlertService {


    private final AlertPersistencyService elasticAlertService;

    public RestAlertServiceImpl(AlertPersistencyService elasticAlertService) {
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
        alertQueryBuilder.filterByClassification(alertQuery.getClassification());
        alertQueryBuilder.filterByAlertsIds(alertQuery.getAlertIds());
        alertQueryBuilder.filterByMaxScore(alertQuery.getMaxScore());
        alertQueryBuilder.filterByMinScore(alertQuery.getMinScore());
        alertQueryBuilder.filterByTags(alertQuery.getTags());
        alertQueryBuilder.filterByIndicatorNams(alertQuery.getIndicatorsType());
        alertQueryBuilder.filterByUserName(alertQuery.getUsersId());
        alertQueryBuilder.filterByStartDate(Integer.toUnsignedLong(alertQuery.getStartTimeFrom().intValue()));
        alertQueryBuilder.filterByEndDate(Integer.toUnsignedLong(alertQuery.getStartTimeTo().intValue()));
        List<String> severity = new ArrayList<>();
        alertQuery.getSeverity().forEach(severityParam -> {
            severity.add(severityParam.toString());
        });
        alertQueryBuilder.filterBySeverity(severity);
        List<String> feedback = new ArrayList<>();
        alertQuery.getSort().forEach(feedbackParam -> {
            feedback.add(feedbackParam.toString());
        });
        alertQueryBuilder.filterByFeedback(feedback);
        if (!CollectionUtils.isEmpty(alertQuery.getSort())) {
            List<Sort.Order> orders = new ArrayList<>();
            alertQuery.getSort().forEach(s -> {
                String[] params = s.split(":");
                Sort.Direction direction = Sort.Direction.fromString(params[0]);
                orders.add(new Sort.Order(direction, params[1]));

            });
            alertQueryBuilder.sortField(new Sort(orders));
        }
        AlertQuery convertedAlertQuery = alertQueryBuilder.build();
        return convertedAlertQuery;
    }

    @Override
    public List<presidio.webapp.model.Alert> getAlertsByUserId(String userId) {
        Page<presidio.output.domain.records.alerts.Alert> alerts = elasticAlertService.findByUserId(userId, new PageRequest(0, 10));
        if (alerts.hasContent()) {
            List restAlerts = new ArrayList();
            alerts.forEach(alert -> restAlerts.add(createRestAlert(alert)));
            return restAlerts;
        }
        return null;
    }

    private presidio.webapp.model.Alert createRestAlert(presidio.output.domain.records.alerts.Alert alert) {
        presidio.webapp.model.Alert restAlert = new presidio.webapp.model.Alert();
        restAlert.setScore(Double.valueOf(alert.getScore()).intValue());
        restAlert.setEndDate(Long.valueOf(alert.getEndDate()).intValue());
        restAlert.setStartDate(Long.valueOf(alert.getStartDate()).intValue());
        restAlert.setId(alert.getId());
        restAlert.setClassifiation(alert.getClassifications());
        restAlert.setUsername(alert.getUserName());
        return restAlert;
    }
}
