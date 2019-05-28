package presidio.output.processor.services.alert.indicator;

import edu.emory.mathcs.backport.java.util.Collections;
import fortscale.common.general.CommonStrings;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;
import presidio.ade.domain.record.aggregated.AggregatedFeatureType;
import presidio.ade.domain.record.aggregated.ScoredFeatureAggregationRecord;
import presidio.ade.domain.record.aggregated.SmartAggregationRecord;
import presidio.output.domain.records.alerts.Alert;
import presidio.output.domain.records.alerts.AlertEnums;
import presidio.output.domain.records.alerts.Indicator;
import presidio.output.processor.config.IndicatorConfig;
import presidio.output.processor.config.SupportingInformationConfig;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class IndicatorsGeneratorForFeatureAggr implements IndicatorsGenerator {

    private final SupportingInformationConfig config;

    public IndicatorsGeneratorForFeatureAggr(SupportingInformationConfig config) {
        this.config = config;
    }

    @Override
    public List<Indicator> generateIndicators(SmartAggregationRecord smartAggregationRecord, Alert alert) {
        AdeAggregationRecord adeAggregationRecord = smartAggregationRecord.getAggregationRecord();
        List<Indicator> indicators = new ArrayList<>();
        IndicatorConfig indicatorConfig = config.getIndicatorConfig(adeAggregationRecord.getFeatureName());

        Indicator indicator = new Indicator(alert.getId());
        indicator.setName(indicatorConfig.getName());
        indicator.setStartDate(Date.from(adeAggregationRecord.getStartInstant()));
        indicator.setEndDate(Date.from(adeAggregationRecord.getEndInstant()));
        indicator.setAnomalyValue(String.valueOf(adeAggregationRecord.getFeatureValue()));
        indicator.setSchema(indicatorConfig.getSchema());
        indicator.setType(AlertEnums.IndicatorTypes.valueOf(indicatorConfig.getType()));
        indicator.setScore(((ScoredFeatureAggregationRecord) adeAggregationRecord).getScore());
        indicator.setScoreContribution(smartAggregationRecord.getContribution());
        Map<String, String> contexts = adeAggregationRecord.getContext();
        indicator.setContexts(contexts);
        indicators.add(indicator);

        return indicators;
    }

    @Override
    public String getType() {
        return AggregatedFeatureType.FEATURE_AGGREGATION.name();
    }
}
