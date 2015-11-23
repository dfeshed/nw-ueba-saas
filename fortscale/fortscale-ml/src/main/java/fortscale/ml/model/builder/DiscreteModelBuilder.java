package fortscale.ml.model.builder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fortscale.ml.model.Model;
import fortscale.ml.model.prevalance.field.DiscreteDataModel;
import fortscale.utils.logging.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class DiscreteModelBuilder implements IModelBuilder {
    private static final Logger logger = Logger.getLogger(DiscreteModelBuilder.class);
    public static final String MODEL_BUILDER_TYPE = "discrete";

    private Pattern ignoreValues;

    @JsonCreator
    public DiscreteModelBuilder(@JsonProperty("ignorePattern") String ignorePattern) {
        if (ignorePattern != null) {
            ignoreValues = Pattern.compile(ignorePattern);
        }
    }

    @Override
    public Model build(Object modelBuilderData) {
        List<Object> values = castModelBuilderData(modelBuilderData);
        DiscreteDataModel model = new DiscreteDataModel(ignoreValues);
        try {
            model.setFeatureCounts(countFeatureValues(values, model));
        } catch (Exception e) {
            logger.error("got an exception while trying to build DiscreteDataModel", e);
        }
        return model;
    }

    private List<Double> countFeatureValues(List<Object> values, DiscreteDataModel model) {
        Map<String, Double> featureValueToCountMap = new HashMap<>();
        for (Object value : values) {
            String featureValue = model.getFeatureValue(value);
            if (featureValue != null) {
                Double count = featureValueToCountMap.get(featureValue);
                if (count == null) {
                    count = 0d;
                }
                featureValueToCountMap.put(featureValue, count + 1);
            }
        }
        return new ArrayList<>(featureValueToCountMap.values());
    }

    private List<Object> castModelBuilderData(Object modelBuilderData) {
        if (modelBuilderData == null) {
            throw new IllegalArgumentException();
        }
        if (!(modelBuilderData instanceof List)) {
            String errorMsg = "got illegal modelBuilderData type - probably bad ASL";
            logger.error(errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }
        return (List<Object>) modelBuilderData;
    }
}
