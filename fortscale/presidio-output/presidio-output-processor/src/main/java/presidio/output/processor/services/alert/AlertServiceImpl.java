package presidio.output.processor.services.alert;

import fortscale.utils.logging.Logger;
import fortscale.utils.pagination.PageIterator;
import org.apache.commons.collections.CollectionUtils;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;
import presidio.ade.domain.record.aggregated.SmartRecord;
import presidio.output.domain.records.alerts.Alert;
import presidio.output.domain.records.alerts.AlertEnums;
import presidio.output.domain.records.users.User;
import presidio.output.domain.services.alerts.AlertPersistencyService;

import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by efratn on 24/07/2017.
 */
public class AlertServiceImpl implements AlertService {

    private static final Logger logger = Logger.getLogger(AlertServiceImpl.class);

    private final AlertEnumsSeverityService alertEnumsSeverityService;
    private final AlertPersistencyService alertPersistencyService;


    private AlertNamingService alertNamingService;

    public AlertServiceImpl(AlertPersistencyService alertPersistencyService, AlertEnumsSeverityService alertEnumsSeverityService, AlertNamingService alertNamingService) {
        this.alertPersistencyService = alertPersistencyService;
        this.alertEnumsSeverityService = alertEnumsSeverityService;
        this.alertNamingService = alertNamingService;
    }

    @Override
    public Alert generateAlert(SmartRecord smart, User user) {
        double score = smart.getScore();
        if (score < 50) {
            return null;
        }

        String id = smart.getId();
        List<String> classification = alertNamingService.alertNamesFromIndicatorsByPriority(extractIndicatorsNames(smart));
        String userName = smart.getContextId();
        AlertEnums.AlertType type = AlertEnums.AlertType.GLOBAL; //TODO change this to "AlertClassification"
        long startDate = smart.getStartInstant().getLong(ChronoField.INSTANT_SECONDS);
        long endDate = smart.getEndInstant().getLong(ChronoField.INSTANT_SECONDS);
        int indicatorsNum = smart.getAggregationRecords().size();
        //TODO- on the new ADE SMART POJO there should be a dedicated field for Daily/Hourly
        AlertEnums.AlertTimeframe timeframe = AlertEnums.AlertTimeframe.DAILY;
        AlertEnums.AlertSeverity severity = alertEnumsSeverityService.severity(score);
        return new Alert(user.getUserId(), classification, userName, type, startDate, endDate, score, indicatorsNum, timeframe, severity);
    }

    @Override
    public void save(List<Alert> alerts) {
        alertPersistencyService.save(alerts);
    }

    private List<String> extractIndicatorsNames(SmartRecord smart) {
        return smart.getAggregationRecords().stream().map(AdeAggregationRecord::getFeatureName).collect(Collectors.toList());
    }
}
