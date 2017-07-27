package presidio.output.processor.services.alert;

import fortscale.domain.SMART.EntityEvent;
import fortscale.utils.logging.Logger;
import fortscale.utils.pagination.PageIterator;
import org.springframework.beans.factory.annotation.Autowired;
import presidio.output.domain.records.Alert;
import presidio.output.domain.records.AlertEnums;
import presidio.output.domain.services.AlertPersistencyService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by efratn on 24/07/2017.
 */
public class AlertServiceImpl implements AlertService {

    private static final Logger logger = Logger.getLogger(AlertServiceImpl.class);

    @Autowired
    private AlertPersistencyService alertPersistencyService;

    public AlertServiceImpl(AlertPersistencyService alertPersistencyService) {
        this.alertPersistencyService = alertPersistencyService;
    }

    @Override
    public void generateAlerts(PageIterator<EntityEvent> smartPageIterator) {
        List<Alert> alerts = new ArrayList<Alert>();

        while(smartPageIterator.hasNext()) {
            List<EntityEvent> smarts = smartPageIterator.next();

            smarts.stream().forEach(smart -> {alerts.add(convertSmartToAlert(smart));});
            break; //TODO !!! remove this once ADE Team will implement SmartPageIterator.hasNext(). currently only one page is returned.
        }

        alertPersistencyService.save(alerts);
        logger.debug("{} output alerts were generated", alerts.size());
    }

    private Alert convertSmartToAlert(EntityEvent smart) {

        String id = smart.getId();
        String userName = smart.getContextId();
        AlertEnums.AlertType type = AlertEnums.AlertType.GLOBAL; //TODO change this to "AlertClassification"
        long startDate = smart.getStart_time_unix();
        long endDate = smart.getEnd_time_unix();
        double score = smart.getScore();
        int indicatorsNum = smart.getAggregated_feature_events().size();
        //TODO- on the new ADE SMART POJO there should be a dedicated field for Daily/Hourly
        AlertEnums.AlertTimeframe timeframe = AlertEnums.AlertTimeframe.DAILY;
        //TODO- calculate Severity, currently hard-coded
        AlertEnums.AlertSeverity severity = AlertEnums.AlertSeverity.CRITICAL;

        return new Alert(id, userName, type, startDate, endDate, score, indicatorsNum, timeframe, severity);
    }
}
