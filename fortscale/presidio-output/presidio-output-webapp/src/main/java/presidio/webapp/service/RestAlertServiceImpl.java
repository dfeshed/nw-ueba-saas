package presidio.webapp.service;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import presidio.output.domain.records.alerts.AlertQuery;
import presidio.output.domain.services.alerts.AlertPersistencyService;
import presidio.webapp.dto.Alert;
import presidio.webapp.restquery.RestAlertQuery;

@Service
public class RestAlertServiceImpl implements RestAlertService {


    private final AlertPersistencyService elasticAlertService;

    public RestAlertServiceImpl(AlertPersistencyService elasticAlertService) {
        this.elasticAlertService = elasticAlertService;
    }

    @Override
    public Alert getAlertById(String id) {
        presidio.output.domain.records.alerts.Alert alertData = elasticAlertService.findOne(id);

        Alert resultAlert = null;
        if (alertData != null) {
            resultAlert = createResult(alertData);
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
    public Page<presidio.output.domain.records.alerts.Alert> getAlerts(RestAlertQuery restAlertQuery) {
        AlertQuery alertQuery = createQuery(restAlertQuery);
        Page<presidio.output.domain.records.alerts.Alert> alerts = elasticAlertService.find(alertQuery);

        return alerts;
    }

    private AlertQuery createQuery(RestAlertQuery restAlertQuery) {
        AlertQuery.AlertQueryBuilder alertQueryBuilder = new AlertQuery.AlertQueryBuilder();
        alertQueryBuilder.filterByUserName(restAlertQuery.getUserName());
        alertQueryBuilder.filterByClassification(restAlertQuery.getClassification());
        alertQueryBuilder.filterByStartDate(restAlertQuery.getStartDate());
        alertQueryBuilder.filterByEndDate(restAlertQuery.getEndDate());
        alertQueryBuilder.filterBySeverity(restAlertQuery.getSeverity());
        alertQueryBuilder.sortField(restAlertQuery.getSortField(), restAlertQuery.isAscendingOrder());
        AlertQuery alertQuery = alertQueryBuilder.build();
        return alertQuery;
    }
}
