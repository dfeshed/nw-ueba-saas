package presidio.output.processor.services.alert.indicator.enricher;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fortscale.common.feature.FeatureValue;
import fortscale.common.feature.MultiKeyFeature;
import fortscale.common.feature.MultiKeyHistogram;
import presidio.output.domain.records.alerts.Indicator;
import presidio.output.processor.config.IndicatorConfig;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

@JsonAutoDetect(
        creatorVisibility = JsonAutoDetect.Visibility.ANY,
        fieldVisibility = JsonAutoDetect.Visibility.NONE,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE)
public class AddContextOfMaximumValueIndicatorEnricher extends FeatureBucketIndicatorEnricher {
    @JsonCreator
    public AddContextOfMaximumValueIndicatorEnricher(
            @JsonProperty("featureBucketConfName") String featureBucketConfName,
            @JsonProperty("aggregatedFeatureConfName") String aggregatedFeatureConfName,
            @JsonProperty("pageSize") Integer pageSize) {

        super(featureBucketConfName, aggregatedFeatureConfName, pageSize);
    }

    @Override
    void enrichIndicator(IndicatorConfig indicatorConfig, Indicator indicator, FeatureValue featureValue) {
        MultiKeyHistogram multiKeyHistogram = (MultiKeyHistogram)featureValue;
        Map<String, String> contextOfMaximumValue = multiKeyHistogram.getHistogram().entrySet().stream()
                .max(Comparator.comparing(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .map(MultiKeyFeature::getFeatureNameToValue)
                .orElse(Collections.emptyMap());
        indicator.getContexts().putAll(contextOfMaximumValue);
    }
}
