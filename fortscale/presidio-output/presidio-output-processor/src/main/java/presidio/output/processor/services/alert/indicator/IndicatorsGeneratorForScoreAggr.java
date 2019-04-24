package presidio.output.processor.services.alert.indicator;

import fortscale.common.general.CommonStrings;
import fortscale.utils.recordreader.RecordReaderFactoryService;
import org.apache.commons.lang3.StringUtils;
import presidio.ade.domain.record.AdeRecord;
import presidio.ade.domain.record.AdeRecordReader;
import presidio.ade.domain.record.AdeScoredEnrichedRecordReader;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;
import presidio.ade.domain.record.aggregated.AggregatedFeatureType;
import presidio.ade.domain.record.aggregated.ScoredFeatureAggregationRecord;
import presidio.ade.domain.record.aggregated.SmartAggregationRecord;
import presidio.ade.domain.record.enriched.AdeScoredEnrichedRecord;
import presidio.ade.sdk.aggregation_records.splitter.ScoreAggregationRecordContributors;
import presidio.ade.sdk.common.AdeManagerSdk;
import presidio.output.domain.records.alerts.Alert;
import presidio.output.domain.records.alerts.AlertEnums;
import presidio.output.domain.records.alerts.Indicator;
import presidio.output.domain.records.events.EnrichedEvent;
import presidio.output.processor.config.IndicatorConfig;
import presidio.output.processor.config.SupportingInformationConfig;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class IndicatorsGeneratorForScoreAggr implements IndicatorsGenerator {

    public static final String CONTEXT_PREFIX = "context.";

    private final SupportingInformationConfig config;
    private final AdeManagerSdk adeManagerSdk;
    private final RecordReaderFactoryService recordReaderFactoryService;

    public IndicatorsGeneratorForScoreAggr(SupportingInformationConfig config, AdeManagerSdk adeManagerSdk, RecordReaderFactoryService recordReaderFactoryService) {
        this.config = config;
        this.adeManagerSdk = adeManagerSdk;
        this.recordReaderFactoryService = recordReaderFactoryService;
    }

    public List<Indicator> generateIndicators(SmartAggregationRecord smartAggregationRecord, Alert alert) {
        List<Indicator> indicators = new ArrayList<>();

        AdeAggregationRecord adeAggregationRecord = smartAggregationRecord.getAggregationRecord();
        IndicatorConfig indicatorConfig = config.getIndicatorConfig(adeAggregationRecord.getFeatureName());

        List<String> splitFieldNames = indicatorConfig.getSplitFields() == null? new ArrayList<>(): new ArrayList<>(indicatorConfig.getSplitFields());
        splitFieldNames.replaceAll(field -> translateOutputToAdeName(field));

        ScoreAggregationRecordContributors scoreAggregationRecordContributors = adeManagerSdk.splitScoreAggregationRecordToContributors(adeAggregationRecord, splitFieldNames);

        for (ScoreAggregationRecordContributors.Contributor scoreAggregationRecordContributor : scoreAggregationRecordContributors.getContributors()) {

            Indicator indicator =
                    AdeScoredEnrichedRecord.class.equals(scoreAggregationRecordContributors.getScoredRecordClass())?
                            buildScoreAggrIndicator(smartAggregationRecord, alert, indicatorConfig, scoreAggregationRecordContributor):
                            buildFeatureAggrIndicator(smartAggregationRecord, alert, indicatorConfig, scoreAggregationRecordContributor);

            indicators.add(indicator);
        }
        return indicators;
    }

    public String getType() {
        return AggregatedFeatureType.SCORE_AGGREGATION.name();
    }

    private Indicator buildFeatureAggrIndicator(SmartAggregationRecord smartAggregationRecord, Alert alert, IndicatorConfig indicatorConfig, ScoreAggregationRecordContributors.Contributor scoreAggregationRecordContributor) {
        Indicator indicator = new Indicator(alert.getId());
        indicator.setName(indicatorConfig.getName());

        ScoredFeatureAggregationRecord scoredFeatureAggregationRecord = (ScoredFeatureAggregationRecord)scoreAggregationRecordContributor.getFirstScoredRecord();

        indicator.setAnomalyValue(String.valueOf(scoredFeatureAggregationRecord.getFeatureValue()));
        indicator.setSchema(indicatorConfig.getSchema());
        indicator.setType(AlertEnums.IndicatorTypes.valueOf(indicatorConfig.getType()));
        indicator.setScore(scoredFeatureAggregationRecord.getScore());
        indicator.setScoreContribution(scoreAggregationRecordContributor.getContributionRatio()*smartAggregationRecord.getContribution());
        // add split fields
        Map<String, String> adeContexts = scoreAggregationRecordContributor.getContextFieldNameToValueMap().getFeatureNameToValue();
        Map<String, String> contexts = adeContexts.entrySet().stream()
                .filter(entry -> entry.getValue() != null)
                .collect(Collectors.toMap(entry -> translateAdeNameToOutput(entry.getKey()),
                        entry -> entry.getValue()));

        AdeRecordReader firstRecordReader = (AdeRecordReader) recordReaderFactoryService.getRecordReader(scoreAggregationRecordContributor.getFirstScoredRecord());

        // add indicator context
        contexts.put(alert.getEntityType(), firstRecordReader.getContext(alert.getEntityType()));

        // add model context
        indicatorConfig.getModelContextFields().forEach(modelContextField -> contexts.put(modelContextField, firstRecordReader.getContext(modelContextField)));

        indicator.setContexts(contexts);
        indicator.setStartDate(Date.from(scoredFeatureAggregationRecord.getStartInstant()));
        indicator.setEndDate(Date.from(scoredFeatureAggregationRecord.getEndInstant()));
        return indicator;
    }

    private Indicator buildScoreAggrIndicator(SmartAggregationRecord smartAggregationRecord, Alert alert, IndicatorConfig indicatorConfig, ScoreAggregationRecordContributors.Contributor scoreAggregationRecordContributor) {
        Indicator indicator = new Indicator(alert.getId());

        indicator.setName(indicatorConfig.getName());
        AdeScoredEnrichedRecordReader firstRecordReader = (AdeScoredEnrichedRecordReader)recordReaderFactoryService.getRecordReader(scoreAggregationRecordContributor.getFirstScoredRecord());
        AdeScoredEnrichedRecordReader lastRecordReader = (AdeScoredEnrichedRecordReader)recordReaderFactoryService.getRecordReader(scoreAggregationRecordContributor.getLastScoredRecord());
        indicator.setStartDate(Date.from(firstRecordReader.getDate_time()));
        indicator.setEndDate(Date.from(lastRecordReader.getDate_time()));
        indicator.setSchema(indicatorConfig.getSchema());
        indicator.setType(AlertEnums.IndicatorTypes.valueOf(indicatorConfig.getType()));
        indicator.setScoreContribution(scoreAggregationRecordContributor.getContributionRatio()*smartAggregationRecord.getContribution());
        Map<String, String> adeContexts = scoreAggregationRecordContributor.getContextFieldNameToValueMap().getFeatureNameToValue();

        // add split fields
        Map<String, String> contexts = adeContexts.entrySet().stream()
                .filter(entry -> entry.getValue() != null)
                .collect(Collectors.toMap(entry -> translateAdeNameToOutput(entry.getKey()),
                        entry -> entry.getValue()));
        // add indicator context
        contexts.put(alert.getEntityType(), firstRecordReader.getContext(alert.getEntityType()));

        // add model context
        indicatorConfig.getModelContextFields().forEach(modelContextField -> contexts.put(modelContextField, firstRecordReader.getContext(modelContextField)));

        // add anomaly field
        contexts.put(indicatorConfig.getAnomalyDescriptior().getAnomalyField(),
                firstRecordReader.get(translateOutputToAdeName(indicatorConfig.getAnomalyDescriptior().getAnomalyField())).toString());

        String featureValue  = AlertEnums.IndicatorTypes.STATIC_INDICATOR.name().equals(indicatorConfig.getType())?
                StringUtils.EMPTY:
                contexts.get(indicatorConfig.getAnomalyDescriptior().getAnomalyField());
        indicator.setContexts(contexts);

        indicator.setAnomalyValue(featureValue);

        return indicator;
    }

    private String translateAdeNameToOutput(String adeName) {
        return AdeRecord.START_INSTANT_FIELD.equals(adeName)?
                EnrichedEvent.EVENT_DATE_FIELD_NAME:
                adeName.replace(CONTEXT_PREFIX,"");
    }

    private String translateOutputToAdeName(String outputName) {
        return EnrichedEvent.EVENT_DATE_FIELD_NAME.equals(outputName)?
                AdeRecord.START_INSTANT_FIELD:
                CONTEXT_PREFIX + outputName;
    }
}
