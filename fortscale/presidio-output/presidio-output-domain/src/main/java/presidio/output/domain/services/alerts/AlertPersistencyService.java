package presidio.output.domain.services.alerts;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import presidio.output.commons.services.alert.AlertEnums;
import presidio.output.domain.records.alerts.*;

import java.util.Collection;
import java.util.List;

public interface AlertPersistencyService {

    Alert save(Alert alert);

    Iterable<Alert> save(List<Alert> alerts);

    void delete(Alert alert);

    Alert findOne(String id);

    Iterable<Alert> findAll();

    Page<Alert> findByUserName(String userName, PageRequest pageRequest);

    Page<Alert> findByUserId(String userId, PageRequest pageRequest);

    Page<Alert> findByUserIdIn(Collection<String> userId, PageRequest pageRequest);

    Page<Alert> findById(String id, PageRequest pageRequest);

    Page<Alert> find(AlertQuery alertQuery);

    Indicator findIndicatorById(String indicatorId);

    IndicatorSummary findIndicatorSummaryById(String indicatorId);

    Page<Indicator> findIndicatorsByAlertId(String alertId, PageRequest pageRequest);

    Page<IndicatorSummary> findIndicatorsSummaryByAlertId(String alertId, PageRequest pageRequest);

    Page<IndicatorEvent> findIndicatorEventsByIndicatorId(String indicatorId, PageRequest pageRequest);

    void updateAlertFeedback(String alertId, AlertEnums.AlertFeedback feedback);

}