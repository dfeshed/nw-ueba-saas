package fortscale.ml.model.builder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fortscale.ml.model.Model;
import fortscale.ml.model.prevalance.field.DiscreteDataModel;
import fortscale.utils.logging.Logger;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

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
        return new DiscreteDataModel(countFeatureValues(values));
    }

    @Override
    public double calculateScore(Object value, Model model) {
        Pair<Object, Double> featureAndCount = (Pair<Object, Double>) value;
        String featureValue = getFeatureValue(featureAndCount.getKey());
        if (featureValue == null) {
            return 0;
        }
        return model.calculateScore(featureAndCount.getValue());
    }

    private List<Double> countFeatureValues(List<Object> values) {
        Map<String, Double> featureValueToCountMap = new HashMap<>();
        for (Object value : values) {
            String featureValue = getFeatureValue(value);
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

    private String getFeatureValue(Object value){
        if (value == null) {
            return null;
        }
        String s = value.toString();
        if (StringUtils.isBlank(s) || (ignoreValues != null && ignoreValues.matcher(s).matches())) {
            return null;
        }
        return s;
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
