package presidio.output.domain.services.alerts;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import presidio.output.domain.records.alerts.*;
import presidio.output.domain.repositories.AlertRepository;
import presidio.output.domain.repositories.IndicatorEventRepository;
import presidio.output.domain.repositories.IndicatorRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class AlertPersistencyServiceImpl implements AlertPersistencyService {

    @Autowired
    private AlertRepository alertRepository;

    @Autowired
    private IndicatorRepository indicatorRepository;

    @Autowired
    private IndicatorEventRepository indicatorEventRepository;

    public Alert save(Alert alert) {
        return alertRepository.save(alert);
    }

    public Iterable<Alert> save(List<Alert> alerts) {
        //atomic save for the entire alert entities

        // save alerts
        Iterable<Alert> savedAlerts = alertRepository.save(alerts);

        // save indicators
        List<Indicator> indicators = new ArrayList<Indicator>();
        alerts.stream().forEach(alert -> indicators.addAll(alert.getIndicators()));
        if (CollectionUtils.isNotEmpty(indicators)) {
            indicatorRepository.save(indicators);
        }

        // save events
        List<IndicatorEvent> events = new ArrayList<IndicatorEvent>();
        indicators.stream().forEach(indicator -> events.addAll(indicator.getEvents()));
        if (CollectionUtils.isNotEmpty(events)) {
            indicatorEventRepository.save(events);
        }

        return savedAlerts;
    }

    public void delete(Alert alert) {

        // delete alert
        alertRepository.delete(alert);

        // TODO: delete supporting information
    }

    @Override
    public Alert findOne(String id) {
        return alertRepository.findOne(id);
    }

    @Override
    public Iterable<Alert> findAll() {
        return alertRepository.findAll();
    }

    @Override
    public Page<Alert> findByUserName(String userName, PageRequest pageRequest) {
        return alertRepository.findByUserName(userName, pageRequest);
    }

    @Override
    public Page<Alert> findByUserId(String userId, PageRequest pageRequest) {
        return alertRepository.findByUserId(userId, pageRequest);
    }

    @Override
    public Page<Alert> findById(String id, PageRequest pageRequest) {
        return alertRepository.findById(id, pageRequest);
    }

    @Override
    public Page<Alert> find(AlertQuery alertQuery) {
        return alertRepository.search(new AlertElasticsearchQueryBuilder(alertQuery).build());
    }

    @Override
    public Page<Alert> findByUserIdIn(Collection<String> userId, PageRequest pageRequest) {
        return alertRepository.findByUserIdIn(userId, pageRequest);
    }

    @Override
    public Indicator findIndicatorById(String indicatorId) {
        return indicatorRepository.findIndicatorById(indicatorId);
    }

    @Override
    public IndicatorSummary findIndicatorSummaryById(String indicatorId) {
        return indicatorRepository.findIndicatorSummaryById(indicatorId);
    }

    @Override
    public Page<Indicator> findIndicatorsByAlertId(String alertId, PageRequest pageRequest) {
        return indicatorRepository.findByAlertId(alertId, pageRequest);
    }

    @Override
    public Page<IndicatorSummary> findIndicatorsSummaryByAlertId(String alertId, PageRequest pageRequest) {
        return indicatorRepository.findIndicatorsSummaryByAlertId(alertId, pageRequest);
    }

    @Override
    public Page<IndicatorEvent> findIndicatorEventsByIndicatorId(String indicatorId, PageRequest pageRequest) {
        return indicatorEventRepository.findIndicatorEventsByIndicatorId(indicatorId, pageRequest);
    }


}