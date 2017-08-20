package presidio.output.processor.services.alert;

import fortscale.domain.SMART.EntityEvent;
import fortscale.utils.logging.Logger;
import fortscale.utils.pagination.PageIterator;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import presidio.output.domain.records.alerts.Alert;
import presidio.output.domain.records.alerts.AlertEnums;
import presidio.output.domain.services.alerts.AlertEnumsSeverityService;
import presidio.output.domain.services.alerts.AlertPersistencyService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by efratn on 24/07/2017.
 */
public class AlertServiceImpl implements AlertService {

    private static final Logger logger = Logger.getLogger(AlertServiceImpl.class);


    private AlertPersistencyService alertPersistencyService;

    private AlertEnumsSeverityService alertEnumsSeverityService;

    public AlertServiceImpl(AlertPersistencyService alertPersistencyService,AlertEnumsSeverityService alertEnumsSeverityService) {
        this.alertPersistencyService = alertPersistencyService;
        this.alertEnumsSeverityService=alertEnumsSeverityService;
    }

    @Override
    public void generateAlerts(PageIterator<EntityEvent> smartPageIterator) {
        List<Alert> alerts = new ArrayList<Alert>();

        while (smartPageIterator.hasNext()) {
            List<EntityEvent> smarts = smartPageIterator.next();

            smarts.stream().forEach(smart -> {
                Alert alert = convertSmartToAlert(smart);
                if (alert != null)
                    alerts.add(alert);
            });
            break; //TODO !!! remove this once ADE Team will implement SmartPageIterator.hasNext(). currently only one page is returned.
        }

        if (CollectionUtils.isNotEmpty(alerts)) {
            alertPersistencyService.save(alerts);
        }
        logger.debug("{} output alerts were generated", alerts.size());
    }

    private Alert convertSmartToAlert(EntityEvent smart) {
        double score = smart.getScore();
        if (score >= 50) {
            String id = smart.getId();
            String userName = smart.getContextId();
            AlertEnums.AlertType type = AlertEnums.AlertType.GLOBAL; //TODO change this to "AlertClassification"
            long startDate = smart.getStart_time_unix();
            long endDate = smart.getEnd_time_unix();
            int indicatorsNum = smart.getAggregated_feature_events().size();
            //TODO- on the new ADE SMART POJO there should be a dedicated field for Daily/Hourly
            AlertEnums.AlertTimeframe timeframe = AlertEnums.AlertTimeframe.DAILY;
            AlertEnums.AlertSeverity severity = alertEnumsSeverityService.severity(score);
            return new Alert(id, userName, type, startDate, endDate, score, indicatorsNum, timeframe, severity);
        }
        return null;
    }
}