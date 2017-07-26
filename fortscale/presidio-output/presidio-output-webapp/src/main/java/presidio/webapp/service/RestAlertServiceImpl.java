package presidio.webapp.service;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import presidio.output.domain.records.AlertQuery;
import presidio.output.domain.services.AlertPersistencyService;
import presidio.output.domain.services.AlertPersistencyServiceImpl;
import presidio.webapp.dto.Alert;
import presidio.webapp.filter.AlertFilter;

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
        presidio.output.domain.records.Alert alertData = elasticAlertService.findOne(id);

        Alert resultAlert = null;
        if (alertData != null) {
            resultAlert = createResult(alertData);
        }

        return resultAlert;
    }

    private Alert createResult(presidio.output.domain.records.Alert alertData) {
        Alert resultAlert = new Alert();
        resultAlert.setId(alertData.getId());
        resultAlert.setName(alertData.getAlertType().name());
        resultAlert.setUsername(alertData.getUserName());
        resultAlert.setIndicatorsNum(alertData.getIndicatorsNum());
        resultAlert.setStartDate(Instant.ofEpochMilli(alertData.getStartDate()));
        return resultAlert;
    }

    @Override
    public List<Alert> getAlerts(AlertFilter alertFilter) {
        List<Alert> result = new ArrayList<>();
        AlertQuery alertQuery = createQuery(alertFilter);
        Page<presidio.output.domain.records.Alert> alerts = elasticAlertService.find(alertQuery);

        if (alerts.hasContent()) {
            alerts.forEach(alert -> result.add(createResult(alert)));
        }
        return result;
    }

    private AlertQuery createQuery(AlertFilter alertFilter) {
        AlertQuery.AlertQueryBuilder alertQueryBuilder = new AlertQuery.AlertQueryBuilder();
        alertQueryBuilder.filterByUserName(alertFilter.getFilterBuUserName());
        Instant filterByStartDate = alertFilter.getFilterByStartDate();
        if (filterByStartDate != null) {
            alertQueryBuilder.filterByStartDate(filterByStartDate.toEpochMilli());
        }
        Instant filterByEndDate = alertFilter.getFilterByEndDate();
        if (filterByEndDate != null) {
            alertQueryBuilder.filterByEndDate(filterByEndDate.toEpochMilli());
        }
        alertQueryBuilder.filterBySeverity(alertFilter.getFilterBySeverity());
        alertQueryBuilder.sortField(alertFilter.getSortField(), alertFilter.isAscendingOrder());
        AlertQuery alertQuery = alertQueryBuilder.build();
        return alertQuery;
    }
}
