package presidio.output.processor.services.alert;

import fortscale.utils.logging.Logger;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;
import presidio.ade.domain.record.aggregated.SmartRecord;
import presidio.output.domain.records.alerts.Alert;
import presidio.output.domain.records.alerts.AlertEnums;
import presidio.output.domain.records.users.User;
import presidio.output.domain.services.alerts.AlertPersistencyService;
import presidio.output.processor.services.user.UserScoreService;

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
    private final UserScoreService userScoreService;

    private final String FiXED_DURATION_HOURLY = "fixed_duration_hourly";
    private final String HOURLY = "hourly";
    private final String DAILY = "daily";

    private AlertClassificationService alertClassificationService;

    public AlertServiceImpl(AlertPersistencyService alertPersistencyService, AlertEnumsSeverityService alertEnumsSeverityService, AlertClassificationService alertClassificationService,
                            UserScoreService userScoreService) {
        this.alertPersistencyService = alertPersistencyService;
        this.alertEnumsSeverityService = alertEnumsSeverityService;
        this.alertClassificationService = alertClassificationService;
        this.userScoreService = userScoreService;
    }

    @Override
    public Alert generateAlert(SmartRecord smart, User user) {
        double score = smart.getScore();
        if (score < 50) {
            return null;
        }

        List<String> classification = alertClassificationService.getAlertClassificationsFromIndicatorsByPriority(extractIndicatorsNames(smart));
        long startDate = smart.getStartInstant().getLong(ChronoField.INSTANT_SECONDS);
        long endDate = smart.getEndInstant().getLong(ChronoField.INSTANT_SECONDS);
        int indicatorsNum = smart.getAggregationRecords().size();
        AlertEnums.AlertSeverity severity = alertEnumsSeverityService.severity(score);

        Alert alert = new Alert(user.getUserId(), classification, user.getUserName(), startDate, endDate, score, indicatorsNum, getStratgyfromSmart(smart), severity, user.getTags());
        userScoreService.increaseUserScoreWithoutSaving(alert, user);

        return alert;
    }

    @Override
    public void save(List<Alert> alerts) {
        alertPersistencyService.save(alerts);
    }

    private AlertEnums.AlertTimeframe getStratgyfromSmart(SmartRecord smart) {
        String strategy = smart.getFixedDurationStrategy().toStrategyName().equals(FiXED_DURATION_HOURLY) ? HOURLY : DAILY;
        return AlertEnums.AlertTimeframe.getAlertTimeframe(strategy);


    }

    private List<String> extractIndicatorsNames(SmartRecord smart) {
        return smart.getAggregationRecords().stream().map(AdeAggregationRecord::getFeatureName).collect(Collectors.toList());
    }
}
