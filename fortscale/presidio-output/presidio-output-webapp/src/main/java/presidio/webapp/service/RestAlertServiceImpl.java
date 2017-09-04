package presidio.webapp.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import presidio.output.domain.records.alerts.AlertQuery;
import presidio.output.domain.services.alerts.AlertPersistencyService;
import presidio.webapp.dto.Alert;
import presidio.webapp.restquery.RestAlertQuery;

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
    public List<presidio.webapp.model.Alert> getAlerts(RestAlertQuery restAlertQuery) {
        AlertQuery alertQuery = createQuery(restAlertQuery);
        Page<presidio.output.domain.records.alerts.Alert> alerts = elasticAlertService.find(alertQuery);
        List restAlerts = new ArrayList();
        alerts.forEach(alert -> restAlerts.add(createRestAlert(alert)));
        return restAlerts;
    }

    private AlertQuery createQuery(RestAlertQuery restAlertQuery) {
        AlertQuery.AlertQueryBuilder alertQueryBuilder = new AlertQuery.AlertQueryBuilder();
        alertQueryBuilder.filterByUserName(restAlertQuery.getUserName());
        alertQueryBuilder.filterByClassification(restAlertQuery.getClassification());
        alertQueryBuilder.filterByStartDate(restAlertQuery.getStartDate());
        alertQueryBuilder.filterByEndDate(restAlertQuery.getEndDate());
        alertQueryBuilder.filterBySeverity(restAlertQuery.getSeverity());
        alertQueryBuilder.sortField(restAlertQuery.getSort());
        alertQueryBuilder.filterByAlertsIds(restAlertQuery.getAlertsIds());
        alertQueryBuilder.filterByFeedback(restAlertQuery.getFeedback());
        alertQueryBuilder.filterByMaxScore(restAlertQuery.getMaxScore());
        alertQueryBuilder.filterByMinScore(restAlertQuery.getMinScore());
        alertQueryBuilder.filterByTags(restAlertQuery.getTags());
        alertQueryBuilder.filterByIndicatorNams(restAlertQuery.getIndicatorNams());
        AlertQuery alertQuery = alertQueryBuilder.build();
        return alertQuery;
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
