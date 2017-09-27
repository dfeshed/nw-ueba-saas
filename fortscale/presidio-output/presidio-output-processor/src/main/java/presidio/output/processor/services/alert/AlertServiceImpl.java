package presidio.output.processor.services.alert;

import fortscale.utils.logging.Logger;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;
import presidio.ade.domain.record.aggregated.SmartRecord;
import presidio.output.domain.records.alerts.Alert;
import presidio.output.domain.records.alerts.AlertEnums;
import presidio.output.domain.records.alerts.Indicator;
import presidio.output.domain.records.users.User;
import presidio.output.domain.services.alerts.AlertPersistencyService;
import presidio.output.processor.services.alert.supportinginformation.SupportingInformationGenerator;
import presidio.output.processor.services.alert.supportinginformation.SupportingInformationGeneratorFactory;
import presidio.output.processor.services.user.UserScoreService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by efratn on 24/07/2017.
 */
public class AlertServiceImpl implements AlertService {

    private static final Logger logger = Logger.getLogger(AlertServiceImpl.class);

    private final AlertEnumsSeverityService alertEnumsSeverityService;
    private final AlertPersistencyService alertPersistencyService;
    private final SupportingInformationGeneratorFactory supportingInformationGeneratorFactory;

    private final String FiXED_DURATION_HOURLY = "fixed_duration_hourly";
    private final String HOURLY = "hourly";
    private final String DAILY = "daily";

    private AlertClassificationService alertClassificationService;

    public AlertServiceImpl(AlertPersistencyService alertPersistencyService, AlertEnumsSeverityService alertEnumsSeverityService, AlertClassificationService alertClassificationService,
                            SupportingInformationGeneratorFactory supportingInformationGeneratorFactory) {
        this.alertPersistencyService = alertPersistencyService;
        this.alertEnumsSeverityService = alertEnumsSeverityService;
        this.alertClassificationService = alertClassificationService;
        this.supportingInformationGeneratorFactory = supportingInformationGeneratorFactory;
    }

    @Override
    public Alert generateAlert(SmartRecord smart, User user, int smartThresholdScoreForCreatingAlert) {
        double score = smart.getScore();
        if (score < smartThresholdScoreForCreatingAlert) {
            return null;
        }
        java.util.Date startDate = Date.from(smart.getStartInstant());
        java.util.Date endDate = Date.from(smart.getEndInstant());
        AlertEnums.AlertSeverity severity = alertEnumsSeverityService.severity(score);
        Alert alert = new Alert(user.getId(), smart.getId(), null, user.getUserName(), startDate, endDate, score, 0, getStrategyFromSmart(smart), severity, user.getTags());

        // supporting information
        List<Indicator> supportingInfo = new ArrayList<Indicator>();
        for (AdeAggregationRecord adeAggregationRecord : smart.getAggregationRecords()) {
            SupportingInformationGenerator supportingInformationGenerator = supportingInformationGeneratorFactory.getSupportingInformationGenerator(adeAggregationRecord.getAggregatedFeatureType().name());
            supportingInfo.addAll(supportingInformationGenerator.generateSupportingInformation(adeAggregationRecord, alert));
        }

        // alert update with indicators information
        alert.setIndicators(supportingInfo);
        alert.setIndicatorsNames(supportingInfo.stream().map(i -> i.getName()).collect(Collectors.toList()));
        alert.setIndicatorsNum(supportingInfo.size());
        List<String> classification = alertClassificationService.getAlertClassificationsFromIndicatorsByPriority(new ArrayList<>(alert.getIndicatorsNames()));
        alert.setClassifications(classification);

        return alert;
    }

    @Override
    public void save(List<Alert> alerts) {
        alertPersistencyService.save(alerts);
    }

    private AlertEnums.AlertTimeframe getStrategyFromSmart(SmartRecord smart) {
        String strategy = smart.getFixedDurationStrategy().toStrategyName().equals(FiXED_DURATION_HOURLY) ? HOURLY : DAILY;
        return AlertEnums.AlertTimeframe.getAlertTimeframe(strategy);


    }

    private List<String> extractIndicatorsNames(SmartRecord smart) {
        return smart.getAggregationRecords().stream().map(AdeAggregationRecord::getFeatureName).collect(Collectors.toList());
    }
}
