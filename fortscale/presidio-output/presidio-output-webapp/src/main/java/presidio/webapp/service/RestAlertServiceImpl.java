package presidio.webapp.service;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import presidio.output.domain.records.alerts.AlertQuery;
import presidio.output.domain.services.alerts.AlertPersistencyService;
import presidio.webapp.dto.Alert;
import presidio.webapp.restquery.RestAlertQuery;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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

    private Alert createResult(presidio.output.domain.records.alerts.Alert alertData) {
        Alert resultAlert = new Alert();
        resultAlert.setId(alertData.getId());
        resultAlert.setAlertClassification(alertData.getAlertType().name());
        resultAlert.setUsername(alertData.getUserName());
        resultAlert.setIndicatorsNum(alertData.getIndicatorsNum());
        resultAlert.setStartDate(Instant.ofEpochMilli(alertData.getStartDate()));
        resultAlert.setEndDate(Instant.ofEpochMilli(alertData.getEndDate()));
        resultAlert.setScore(alertData.getScore());
        return resultAlert;
    }

    @Override
    public List<Alert> getAlerts(RestAlertQuery restAlertQuery) {
        List<Alert> result = new ArrayList<>();
        AlertQuery alertQuery = createQuery(restAlertQuery);
        Page<presidio.output.domain.records.alerts.Alert> alerts = elasticAlertService.find(alertQuery);

        if (alerts.hasContent()) {
            alerts.forEach(alert -> result.add(createResult(alert)));
        }
        return result;
    }

    private AlertQuery createQuery(RestAlertQuery restAlertQuery) {
        AlertQuery.AlertQueryBuilder alertQueryBuilder = new AlertQuery.AlertQueryBuilder();
        alertQueryBuilder.filterByUserName(restAlertQuery.getUserName());
        Instant filterByStartDate = restAlertQuery.getStartDate();
        if (filterByStartDate != null) {
            alertQueryBuilder.filterByStartDate(filterByStartDate.toEpochMilli());
        }
        Instant filterByEndDate = restAlertQuery.getEndDate();
        if (filterByEndDate != null) {
            alertQueryBuilder.filterByEndDate(filterByEndDate.toEpochMilli());
        }
        alertQueryBuilder.filterBySeverity(restAlertQuery.getSeverity());
        alertQueryBuilder.sortField(restAlertQuery.getSortField(), restAlertQuery.isAscendingOrder());
        AlertQuery alertQuery = alertQueryBuilder.build();
        return alertQuery;
    }
}
