package fortscale.aggregation.feature.functions;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import fortscale.aggregation.feature.bucket.AggregatedFeatureConf;
import fortscale.common.feature.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@JsonAutoDetect(
        creatorVisibility = JsonAutoDetect.Visibility.ANY,
        fieldVisibility = JsonAutoDetect.Visibility.NONE,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)
public class AggrFeatureMaxFunc implements IAggrFeatureFunction {
    public static final String AGGR_FEATURE_FUNCTION_TYPE = "aggr_feature_max_func";
    private static final String MAXIMIZE_PARAMETER_NAME = "maximize";
    private static final String CONTEXT_PARAMETER_NAME = "context";

    @JsonCreator
    public AggrFeatureMaxFunc() {
        // C'tor requires no parameters.
    }

    @Override
    public FeatureValue updateAggrFeature(
            AggregatedFeatureConf aggregatedFeatureConf,
            Map<String, Feature> features,
            Feature aggrFeature) {

        // If either the configuration or the map of record fields is missing, return the original aggregated feature.
        if (aggregatedFeatureConf == null || features == null) return aggrFeature.getValue();
        // Extract the name of the field that needs to be maximized.
        String maximizeFieldName = extractMaximizeFieldName(aggregatedFeatureConf);
        // Extract the value of the record's maximize field.
        Double maximizeFieldValue = extractMaximizeFieldValue(maximizeFieldName, features);
        // If the maximize field value is null, return the original aggregated feature.
        if (maximizeFieldValue == null) return aggrFeature.getValue();
        // Extract the original maximum value.
        Double maximumValue = extractMaximumValue(aggrFeature);

        if (maximumValue == null || maximumValue < maximizeFieldValue) {
            List<String> contextFieldNames = extractContextFieldNames(aggregatedFeatureConf);
            MultiKeyFeature multiKeyFeature = new MultiKeyFeature();
            contextFieldNames.forEach(contextFieldName -> {
                Feature feature = features.get(contextFieldName);
                FeatureValue contextFieldValue = feature == null ? null : feature.getValue();
                multiKeyFeature.add(contextFieldName, contextFieldValue == null ? null : contextFieldValue.toString());
            });
            MultiKeyHistogram multiKeyHistogram = new MultiKeyHistogram();
            multiKeyHistogram.set(multiKeyFeature, maximizeFieldValue);
            aggrFeature.setValue(multiKeyHistogram);
            return multiKeyHistogram;
        } else {
            return aggrFeature.getValue();
        }
    }

    private static String extractMaximizeFieldName(AggregatedFeatureConf aggregatedFeatureConf) {
        List<String> maximizeFieldNames = aggregatedFeatureConf.getFeatureNamesMap().get(MAXIMIZE_PARAMETER_NAME);

        if (maximizeFieldNames == null) {
            throw new IllegalArgumentException(String.format("Aggregated feature conf %s " +
                    "is missing the maximize parameter.", aggregatedFeatureConf.getName()));
        } else if (maximizeFieldNames.size() > 1) {
            throw new IllegalArgumentException(String.format("Aggregated feature conf %s " +
                    "contains more than one maximize parameter.", aggregatedFeatureConf.getName()));
        }

        return maximizeFieldNames.get(0);
    }

    private static Double extractMaximizeFieldValue(String maximizeFieldName, Map<String, Feature> nameToFeatureMap) {
        Feature feature = nameToFeatureMap.get(maximizeFieldName);
        FeatureValue maximizeFieldValue = feature == null ? null : feature.getValue();
        if (maximizeFieldValue == null) return null;

        if (!(maximizeFieldValue instanceof FeatureNumericValue)) {
            throw new IllegalArgumentException(String.format("Expected a numeric maximize field " +
                    "but got %s.", maximizeFieldValue.getClass().getSimpleName()));
        }

        Number number = ((FeatureNumericValue)maximizeFieldValue).getValue();
        return number == null ? null : number.doubleValue();
    }

    private static Double extractMaximumValue(Feature aggregatedFeature) {
        FeatureValue featureValue = aggregatedFeature.getValue();
        if (featureValue == null) return null;

        if (!(featureValue instanceof MultiKeyHistogram)) {
            throw new IllegalArgumentException(String.format("Expected a map from context to maximum value " +
                    "but got %s.", featureValue.getClass().getSimpleName()));
        }

        Map<MultiKeyFeature, Double> histogram = ((MultiKeyHistogram)featureValue).getHistogram();

        if (histogram.isEmpty()) {
            return null;
        } else if (histogram.size() > 1) {
            throw new IllegalArgumentException("Expected a map from context to maximum value with one entry.");
        }

        return histogram.values().iterator().next();
    }

    private static List<String> extractContextFieldNames(AggregatedFeatureConf aggregatedFeatureConf) {
        List<String> contextFieldNames = aggregatedFeatureConf.getFeatureNamesMap().get(CONTEXT_PARAMETER_NAME);
        if (contextFieldNames == null) contextFieldNames = Collections.emptyList();
        return contextFieldNames;
    }
}
