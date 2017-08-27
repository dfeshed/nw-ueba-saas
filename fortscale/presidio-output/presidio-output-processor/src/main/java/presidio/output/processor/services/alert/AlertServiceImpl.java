package presidio.output.processor.services.alert;

import fortscale.utils.logging.Logger;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;
import presidio.ade.domain.record.aggregated.SmartRecord;
import presidio.output.domain.records.alerts.Alert;
import presidio.output.domain.records.alerts.AlertEnums;
import presidio.output.domain.records.users.User;
import presidio.output.domain.services.alerts.AlertPersistencyService;

import java.time.temporal.ChronoField;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by efratn on 24/07/2017.
 */
public class AlertServiceImpl implements AlertService {

    private static final Logger logger = Logger.getLogger(AlertServiceImpl.class);

    private final AlertEnumsSeverityService alertEnumsSeverityService;
    private final AlertPersistencyService alertPersistencyService;


    private AlertClassificationService alertClassificationService;

    public AlertServiceImpl(AlertPersistencyService alertPersistencyService, AlertEnumsSeverityService alertEnumsSeverityService, AlertClassificationService alertClassificationService) {
        this.alertPersistencyService = alertPersistencyService;
        this.alertEnumsSeverityService = alertEnumsSeverityService;
        this.alertClassificationService = alertClassificationService;
    }

    @Override
    public Alert generateAlert(SmartRecord smart, User user) {
        double score = smart.getScore();
        if (score < 50) {
            return null;
        }

        List<String> classification = alertClassificationService.getAlertClassificationsFromIndicatorsByPriority(extractIndicatorsNames(smart));
        String userName = smart.getContextId();
        long startDate = smart.getStartInstant().getLong(ChronoField.INSTANT_SECONDS);
        long endDate = smart.getEndInstant().getLong(ChronoField.INSTANT_SECONDS);
        int indicatorsNum = smart.getAggregationRecords().size();
        //TODO- on the new ADE SMART POJO there should be a dedicated field for Daily/Hourly
        AlertEnums.AlertTimeframe timeframe = AlertEnums.AlertTimeframe.DAILY;
        AlertEnums.AlertSeverity severity = alertEnumsSeverityService.severity(score);
        return new Alert(user.getUserId(), classification, userName, startDate, endDate, score, indicatorsNum, timeframe, severity);
    }

    @Override
    public void save(List<Alert> alerts) {
        alertPersistencyService.save(alerts);
    }

    private List<String> extractIndicatorsNames(SmartRecord smart) {
        return smart.getAggregationRecords().stream().map(AdeAggregationRecord::getFeatureName).collect(Collectors.toList());
    }
}
