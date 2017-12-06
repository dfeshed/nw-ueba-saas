package presidio.output.processor.services.alert;

import fortscale.utils.logging.Logger;
import org.apache.commons.collections.CollectionUtils;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;
import presidio.ade.domain.record.aggregated.SmartRecord;
import presidio.output.commons.services.alert.AlertEnums;
import presidio.output.commons.services.alert.AlertSeverityService;
import presidio.output.domain.records.alerts.Alert;
import presidio.output.domain.records.alerts.Indicator;
import presidio.output.domain.records.users.User;
import presidio.output.domain.services.alerts.AlertPersistencyService;
import presidio.output.processor.services.alert.supportinginformation.SupportingInformationGenerator;
import presidio.output.processor.services.alert.supportinginformation.SupportingInformationGeneratorFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by efratn on 24/07/2017.
 */
public class AlertServiceImpl implements AlertService {

    private static final Logger logger = Logger.getLogger(AlertServiceImpl.class);

    private final AlertSeverityService alertEnumsSeverityService;
    private final AlertPersistencyService alertPersistencyService;
    private final AlertSeverityService alertSeverityService;
    private final SupportingInformationGeneratorFactory supportingInformationGeneratorFactory;
    private final double indicatorsContributionLimitForClassification;
    private final int eventsLimit;


    private final String FiXED_DURATION_HOURLY = "fixed_duration_hourly";
    private final String HOURLY = "hourly";
    private final String DAILY = "daily";

    private AlertClassificationService alertClassificationService;

    public AlertServiceImpl(AlertPersistencyService alertPersistencyService,
                            AlertSeverityService alertEnumsSeverityService,
                            AlertClassificationService alertClassificationService,
                            AlertSeverityService alertSeverityService,
                            SupportingInformationGeneratorFactory supportingInformationGeneratorFactory,
                            int eventsLimit,
                            double indicatorsContributionLimitForClassification) {
        this.alertPersistencyService = alertPersistencyService;
        this.alertEnumsSeverityService = alertEnumsSeverityService;
        this.alertClassificationService = alertClassificationService;
        this.alertSeverityService = alertSeverityService;
        this.supportingInformationGeneratorFactory = supportingInformationGeneratorFactory;
        this.eventsLimit = eventsLimit;
        this.indicatorsContributionLimitForClassification = indicatorsContributionLimitForClassification;
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
        Double alertContributionToUserScore = alertSeverityService.getUserScoreContributionFromSeverity(severity);
        Alert alert = new Alert(user.getId(), smart.getId(), null, user.getUserName(), startDate, endDate, score, 0, getStrategyFromSmart(smart), severity, user.getTags(), alertContributionToUserScore);
        // supporting information
        List<Indicator> supportingInfo = new ArrayList<>();
        for (AdeAggregationRecord adeAggregationRecord : smart.getAggregationRecords()) {
            SupportingInformationGenerator supportingInformationGenerator = supportingInformationGeneratorFactory.getSupportingInformationGenerator(adeAggregationRecord.getAggregatedFeatureType().name());
            supportingInfo.addAll(supportingInformationGenerator.generateSupportingInformation(adeAggregationRecord, alert, eventsLimit));
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
                List<String> classification = alertClassificationService.getAlertClassificationsFromIndicatorsByPriority(indicatorsNamesForClassification(createIndicatorNameToContributionMap(supportingInfo)));
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

    private Map<String, Number> createIndicatorNameToContributionMap(List<Indicator> indicators) {
        Map<String, Number> map = new HashMap<>();
        indicators.forEach(indicator -> {
            map.put(indicator.getName(), indicator.getScoreContribution());
        });
        return map;
    }

    private List<String> indicatorsNamesForClassification(Map<String, Number> indicatorsMapNameContribution) {
        List<String> indicatorsNameList = indicatorsNamesByContributionLimit(indicatorsMapNameContribution, indicatorsContributionLimitForClassification);
        if (indicatorsNameList.size() == 0) {
            indicatorsNameList = indicatorsNamesByContributionLimit(indicatorsMapNameContribution, maxContribution(indicatorsMapNameContribution).doubleValue());
        }
        return indicatorsNameList;

    }

    private List<String> indicatorsNamesByContributionLimit(Map<String, Number> indicatorsMapNameContribution, double contributionLimit) {
        List<String> indicatorsNameList = new ArrayList<>();
        for (Map.Entry<String, Number> entry : indicatorsMapNameContribution.entrySet()) {
            if (entry.getValue().doubleValue() >= contributionLimit) {
                indicatorsNameList.add(entry.getKey());
            }
        }
        return indicatorsNameList;
    }

    private Number maxContribution(Map<String, Number> indicatorsMapNameContribution) {
        double max = 0;
        for (Map.Entry<String, Number> entry : indicatorsMapNameContribution.entrySet()) {
            if (entry.getValue().doubleValue() >= max) {
                max = entry.getValue().doubleValue();
            }
        }
        return max;
    }

    private AlertEnums.AlertTimeframe getStrategyFromSmart(SmartRecord smart) {
        String strategy = smart.getFixedDurationStrategy().toStrategyName().equals(FiXED_DURATION_HOURLY) ? HOURLY : DAILY;
        return AlertEnums.AlertTimeframe.getAlertTimeframe(strategy);


    }

    private List<String> extractIndicatorsNames(SmartRecord smart) {
        return smart.getAggregationRecords().stream().map(AdeAggregationRecord::getFeatureName).collect(Collectors.toList());
    }
}
