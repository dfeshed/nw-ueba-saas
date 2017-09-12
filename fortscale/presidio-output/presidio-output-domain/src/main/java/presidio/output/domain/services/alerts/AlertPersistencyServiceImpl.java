package presidio.output.domain.services.alerts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import presidio.output.domain.records.alerts.Alert;
import presidio.output.domain.records.alerts.AlertQuery;
import presidio.output.domain.repositories.AlertRepository;

import java.util.Collection;
import java.util.List;

@Service
public class AlertPersistencyServiceImpl implements AlertPersistencyService {

    @Autowired
    private AlertRepository alertRepository;


    public Alert save(Alert alert) {
        return alertRepository.save(alert);
    }

    public Iterable<Alert> save(List<Alert> alerts) {
        return alertRepository.save(alerts);
    }

    public void delete(Alert alert) {
        alertRepository.delete(alert);
    }

    public Alert findOne(String id) {
        return alertRepository.findOne(id);
    }

    public Iterable<Alert> findAll() {
        return alertRepository.findAll();
    }

    public Page<Alert> findByUserName(String userName, PageRequest pageRequest) {
        return alertRepository.findByUserName(userName, pageRequest);
    }

    public Page<Alert> findByUserId(String userId, PageRequest pageRequest) {
        return alertRepository.findByUserId(userId, pageRequest);
    }

    public Page<Alert> findById(String id, PageRequest pageRequest) {
        return alertRepository.findById(id, pageRequest);
    }

    public Page<Alert> find(AlertQuery alertQuery) {
        return alertRepository.search(new AlertElasticsearchQueryBuilder(alertQuery).build());
    }

    @Override
    public Page<Alert> findByUserIdIn(Collection<String> userId, PageRequest pageRequest) {
        return alertRepository.findByUserIdIn(userId, pageRequest);
    }
}