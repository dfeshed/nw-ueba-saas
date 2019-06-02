package presidio.output.processor.services.alert;

import fortscale.utils.logging.Logger;
import org.apache.commons.collections.CollectionUtils;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;
import presidio.ade.domain.record.aggregated.SmartAggregationRecord;
import presidio.ade.domain.record.aggregated.SmartRecord;
import presidio.output.commons.services.alert.AlertSeverityService;
import presidio.output.domain.records.alerts.Alert;
import presidio.output.domain.records.alerts.AlertEnums;
import presidio.output.domain.records.alerts.Indicator;
import presidio.output.domain.records.entity.Entity;
import presidio.output.domain.services.alerts.AlertPersistencyService;
import presidio.output.processor.services.alert.supportinginformation.SupportingInformationGenerator;
import presidio.output.processor.services.alert.supportinginformation.SupportingInformationGeneratorFactory;
import presidio.output.processor.services.alert.indicator.IndicatorsGeneratorFactory;
import presidio.output.processor.services.alert.indicator.IndicatorsGenerator;

import java.time.Instant;
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

    private final AlertPersistencyService alertPersistencyService;
    private final AlertSeverityService alertSeverityService;
    private final SupportingInformationGeneratorFactory supportingInformationGeneratorFactory;
    private final IndicatorsGeneratorFactory indicatorsGeneratorFactory;
    private final double indicatorsContributionLimitForClassification;
    private final int eventsLimit;
    private final int eventsPageSize;


    private final String FiXED_DURATION_HOURLY = "fixed_duration_hourly";
    private final String HOURLY = "hourly";
    private final String DAILY = "daily";

    private AlertClassificationService alertClassificationService;

    public AlertServiceImpl(AlertPersistencyService alertPersistencyService,
                            AlertClassificationService alertClassificationService,
                            AlertSeverityService alertSeverityService,
                            IndicatorsGeneratorFactory indicatorsGeneratorFactory,
                            SupportingInformationGeneratorFactory supportingInformationGeneratorFactory,
                            int eventsLimit,
                            int eventsPageSize,
                            double indicatorsContributionLimitForClassification) {
        this.alertPersistencyService = alertPersistencyService;
        this.alertClassificationService = alertClassificationService;
        this.alertSeverityService = alertSeverityService;
        this.indicatorsGeneratorFactory = indicatorsGeneratorFactory;
        this.supportingInformationGeneratorFactory = supportingInformationGeneratorFactory;
        this.eventsLimit = eventsLimit;
        this.eventsPageSize = eventsPageSize;
        this.indicatorsContributionLimitForClassification = indicatorsContributionLimitForClassification;
    }

    @Override
    public Alert generateAlert(SmartRecord smart, Entity entity, int smartThresholdScoreForCreatingAlert) {
        double score = smart.getScore();
        if (score < smartThresholdScoreForCreatingAlert) {
            return null;
        }
        java.util.Date startDate = Date.from(smart.getStartInstant());
        java.util.Date endDate = Date.from(smart.getEndInstant());
        AlertEnums.AlertSeverity severity = alertSeverityService.getSeverity(score);
        Double alertContributionToEntityScore = alertSeverityService.getEntityScoreContributionFromSeverity(severity);
        Alert alert = new Alert(entity.getId(), smart.getId(), null, entity.getEntityId(), entity.getEntityName(), startDate, endDate, score, 0, getStrategyFromSmart(smart), severity, entity.getTags(), alertContributionToEntityScore, entity.getEntityType());
        // supporting information
        List<Indicator> supportingInfo = new ArrayList<>();

        for (SmartAggregationRecord smartAggregationRecord : smart.getSmartAggregationRecords()) {
            AdeAggregationRecord aggregationRecord = smartAggregationRecord.getAggregationRecord();
            try {
                IndicatorsGenerator indicatorsGenerator = indicatorsGeneratorFactory.getIndicatorsGenerator(aggregationRecord.getAggregatedFeatureType().name());
                List<Indicator> indicators = indicatorsGenerator.generateIndicators(smartAggregationRecord, alert);
                if (CollectionUtils.isNotEmpty(indicators)) {
                    String indicatorsType = indicators.get(0).getType().name();
                    SupportingInformationGenerator supportingInformationGenerator = supportingInformationGeneratorFactory.getSupportingInformationGenerator(indicatorsType);
                    supportingInfo.addAll(supportingInformationGenerator.generateSupportingInformation(smartAggregationRecord, alert, indicators, eventsLimit, eventsPageSize));
                } else {
                    logger.warn("failed to generate indicators for adeAggregationRecord ID {}, feature {}", aggregationRecord.getId(), aggregationRecord.getFeatureName());
                }
            } catch (Exception ex) {
                logger.warn("failed to generate alert for adeAggregationRecord ID {}, feature {}, exception: {}", aggregationRecord.getId(), aggregationRecord.getFeatureName(), ex);
            }
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
                List<String> classification = alertClassificationService.getAlertClassificationsFromIndicatorsByPriority(getIndicatorsForClassification(createIndicatorNameToContributionMap(supportingInfo)));
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
            map.merge(indicator.getName(), indicator.getScoreContribution(), (v1, v2) -> v1.doubleValue() + v2.doubleValue());
        });
        return map;
    }

    private List<String> getIndicatorsForClassification(Map<String, Number> indicatorsMapNameContribution) {
        List<String> indicatorsNameList = filterIndicatorsByContribution(indicatorsMapNameContribution, indicatorsContributionLimitForClassification);
        if (indicatorsNameList.size() == 0) {
            indicatorsNameList = filterIndicatorsByContribution(indicatorsMapNameContribution, getMaxContributionScore(indicatorsMapNameContribution).doubleValue());
        }
        return indicatorsNameList;

    }

    private List<String> filterIndicatorsByContribution(Map<String, Number> indicatorsMapNameContribution, double contributionLimit) {
        List<String> indicatorsNameList = new ArrayList<>();
        for (Map.Entry<String, Number> entry : indicatorsMapNameContribution.entrySet()) {
            if (entry.getValue().doubleValue() >= contributionLimit) {
                indicatorsNameList.add(entry.getKey());
            }
        }
        return indicatorsNameList;
    }

    private Number getMaxContributionScore(Map<String, Number> indicatorsMapNameContribution) {
        double max = 0;
        for (Map.Entry<String, Number> entry : indicatorsMapNameContribution.entrySet()) {
            if (entry.getValue().doubleValue() >= max) {
                max = entry.getValue().doubleValue();
            }
        }
        return max;
    }

    @Override
    public List<Alert> cleanAlerts(Instant startDate, Instant endDate, String entityType) {
        return alertPersistencyService.removeByTimeRangeAndEntityType(startDate, endDate, entityType);
    }


    private AlertEnums.AlertTimeframe getStrategyFromSmart(SmartRecord smart) {
        String strategy = smart.getFixedDurationStrategy().toStrategyName().equals(FiXED_DURATION_HOURLY) ? HOURLY : DAILY;
        return AlertEnums.AlertTimeframe.getAlertTimeframe(strategy);
    }
}
