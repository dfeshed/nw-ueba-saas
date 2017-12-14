package presidio.output.processor.services.alert;

import fortscale.utils.logging.Logger;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;
import presidio.ade.domain.record.aggregated.SmartAggregationRecord;
import presidio.ade.domain.record.aggregated.SmartRecord;
import presidio.output.domain.records.alerts.AlertEnums;
import presidio.output.commons.services.alert.AlertSeverityService;
import presidio.output.domain.records.alerts.Alert;
import presidio.output.domain.records.alerts.Indicator;
import presidio.output.domain.records.users.User;
import presidio.output.domain.services.alerts.AlertPersistencyService;
import presidio.output.processor.services.alert.supportinginformation.SupportingInformationGenerator;
import presidio.output.processor.services.alert.supportinginformation.SupportingInformationGeneratorFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by efratn on 24/07/2017.
 */
public class AlertServiceImpl implements AlertService {

    private static final Logger logger = Logger.getLogger(AlertServiceImpl.class);

    @Value("${output.events.limit}")
    private Integer eventsLimit;

    private final AlertSeverityService alertEnumsSeverityService;
    private final AlertPersistencyService alertPersistencyService;
    private final AlertSeverityService alertSeverityService;
    private final SupportingInformationGeneratorFactory supportingInformationGeneratorFactory;

    private final String FiXED_DURATION_HOURLY = "fixed_duration_hourly";
    private final String HOURLY = "hourly";
    private final String DAILY = "daily";

    private AlertClassificationService alertClassificationService;

    public AlertServiceImpl(AlertPersistencyService alertPersistencyService,
                            AlertSeverityService alertEnumsSeverityService,
                            AlertClassificationService alertClassificationService,
                            AlertSeverityService alertSeverityService,
                            SupportingInformationGeneratorFactory supportingInformationGeneratorFactory) {
        this.alertPersistencyService = alertPersistencyService;
        this.alertEnumsSeverityService = alertEnumsSeverityService;
        this.alertClassificationService = alertClassificationService;
        this.alertSeverityService = alertSeverityService;
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
        AlertEnums.AlertSeverity severity = alertEnumsSeverityService.getSeverity(score);
        Double alertContributionToUserScore = alertSeverityService.getUserScoreContributionFromSeverity(severity);
        Alert alert = new Alert(user.getId(), smart.getId(), null, user.getUserName(), startDate, endDate, score, 0, getStrategyFromSmart(smart), severity, user.getTags(), alertContributionToUserScore);
        // supporting information
        List<Indicator> supportingInfo = new ArrayList<>();

        for (SmartAggregationRecord smartAggregationRecord : smart.getSmartAggregationRecords()) {
            AdeAggregationRecord aggregationRecord = smartAggregationRecord.getAggregationRecord();
            SupportingInformationGenerator supportingInformationGenerator = supportingInformationGeneratorFactory.getSupportingInformationGenerator(aggregationRecord.getAggregatedFeatureType().name());
            supportingInfo.addAll(supportingInformationGenerator.generateSupportingInformation(aggregationRecord, alert, eventsLimit));
        }

        if (CollectionUtils.isNotEmpty(supportingInfo)) {
            // In case that all the indicators for this alert are static indicators we don't want to save the alert
            boolean storeAlert = false;
            for (Indicator indicator : supportingInfo) {
                if (!indicator.getType().equals(AlertEnums.IndicatorTypes.STATIC_INDICATOR)) {
                    storeAlert = true;
                    break;
                }
            }
            // alert update with indicators information
            if (storeAlert) {
                alert.setIndicators(supportingInfo);
                alert.setIndicatorsNames(supportingInfo.stream().map(i -> i.getName()).collect(Collectors.toList()));
                alert.setIndicatorsNum(supportingInfo.size());
                List<String> classification = alertClassificationService.getAlertClassificationsFromIndicatorsByPriority(alert.getIndicatorsNames());
                alert.setClassifications(classification);
            } else {
                return null;
            }
        }

        return alert;


    }

    @Override
    public void save(List<Alert> alerts) {
        alertPersistencyService.save(alerts);
    }

    @Override
    public List<Alert> cleanAlerts(Instant startDate, Instant endDate) {
        return alertPersistencyService.removeByTimeRange(startDate, endDate);
    }

    private AlertEnums.AlertTimeframe getStrategyFromSmart(SmartRecord smart) {
        String strategy = smart.getFixedDurationStrategy().toStrategyName().equals(FiXED_DURATION_HOURLY) ? HOURLY : DAILY;
        return AlertEnums.AlertTimeframe.getAlertTimeframe(strategy);
    }

    private List<String> extractIndicatorsNames(SmartRecord smart) {
        return smart.getSmartAggregationRecords().stream()
                .map(SmartAggregationRecord::getAggregationRecord)
                .map(AdeAggregationRecord::getFeatureName)
                .collect(Collectors.toList());
    }
}
