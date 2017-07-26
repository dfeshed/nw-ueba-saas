package presidio.webapp.service;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import presidio.output.domain.records.AlertQuery;
import presidio.webapp.dto.Alert;
import presidio.webapp.filter.AlertFilter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class RestAlertServiceImpl implements RestAlertService {


    private final presidio.output.domain.services.AlertService elasticAlertService;

    public RestAlertServiceImpl(presidio.output.domain.services.AlertService elasticAlertService) {
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
        resultAlert.setStartDate(Instant.parse(alertData.getStartDate()));
        return resultAlert;
    }

    @Override
    public List<Alert> getAlerts(AlertFilter alertFilter) {
        List<Alert> result = new ArrayList<>();
        AlertQuery alertQuery = createQuery(alertFilter);
        Page<presidio.output.domain.records.Alert> alerts = elasticAlertService.find(alertQuery);

        if (alerts.hasNext()) {
            alerts.forEach(alert -> result.add(createResult(alert)));
        }
        return result;
    }

    private AlertQuery createQuery(AlertFilter alertFilter) {
        AlertQuery.AlertQueryBuilder alertQueryBuilder = new AlertQuery.AlertQueryBuilder();
        alertQueryBuilder.filterByUserName(alertFilter.getFilterBuUserName());
        alertQueryBuilder.filterByStartDate(alertFilter.getFilterByStartDate());
        alertQueryBuilder.filterByEndDate(alertFilter.getFilterByEndDate());
        alertQueryBuilder.filterBySeverity(alertFilter.getFilterBySeverity());
        alertQueryBuilder.sortField(alertFilter.getSortField(), alertFilter.isAscendingOrder());
        AlertQuery alertQuery = alertQueryBuilder.build();
        return alertQuery;
    }
}
