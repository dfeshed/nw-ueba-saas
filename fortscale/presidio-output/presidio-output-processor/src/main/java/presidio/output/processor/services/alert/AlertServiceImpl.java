package presidio.output.processor.services.alert;

import fortscale.domain.SMART.EntityEvent;
import fortscale.utils.logging.Logger;
import presidio.output.domain.records.alerts.Alert;
import presidio.output.domain.records.alerts.AlertEnums;
import presidio.output.domain.records.users.User;
import presidio.output.domain.services.alerts.AlertPersistencyService;

/**
 * Created by efratn on 24/07/2017.
 */
public class AlertServiceImpl implements AlertService {

    private static final Logger logger = Logger.getLogger(AlertServiceImpl.class);

    private AlertEnumsSeverityService alertEnumsSeverityService;


    public AlertServiceImpl(AlertEnumsSeverityService alertEnumsSeverityService) {
        this.alertEnumsSeverityService = alertEnumsSeverityService;
    }

    @Override
    public Alert generateAlert(EntityEvent smart, User user) {
        double score = smart.getScore();
        if (score < 50) {
            return null;
        }

        String userName = user.getUserName();
        AlertEnums.AlertType type = AlertEnums.AlertType.GLOBAL; //TODO change this to "AlertClassification"
        long startDate = smart.getStart_time_unix();
        long endDate = smart.getEnd_time_unix();
        int indicatorsNum = smart.getAggregated_feature_events().size();
        //TODO- on the new ADE SMART POJO there should be a dedicated field for Daily/Hourly
        AlertEnums.AlertTimeframe timeframe = AlertEnums.AlertTimeframe.DAILY;
        AlertEnums.AlertSeverity severity = alertEnumsSeverityService.severity(score);
        return new Alert(user.getUserId(), userName, type, startDate, endDate, score, indicatorsNum, timeframe, severity);
    }

}
