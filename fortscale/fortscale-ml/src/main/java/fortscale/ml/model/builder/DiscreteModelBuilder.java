package fortscale.ml.model.builder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fortscale.ml.model.Model;
import fortscale.ml.model.prevalance.field.DiscreteDataModel;
import fortscale.utils.logging.Logger;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
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
        Map<String, Double> featureValueToCountMap = castModelBuilderData(modelBuilderData);
        return new DiscreteDataModel(getUnignoredCounts(featureValueToCountMap));
    }

    @Override
    public double calculateScore(Object value, Model model) {
        Pair<String, Double> featureAndCount = (Pair<String, Double>) value;
        return shouldIgnoreFeature(featureAndCount.getKey()) ? 0 : model.calculateScore(featureAndCount.getValue());
    }

    private Collection<Double> getUnignoredCounts(Map<String, Double> featureValueToCountMap) {
        List<Double> counts = new ArrayList<>();
        for (Map.Entry<String, Double> entry : featureValueToCountMap.entrySet()) {
            if (!shouldIgnoreFeature(entry.getKey())) {
                counts.add(entry.getValue());
            }
        }
        return counts;
    }

    private boolean shouldIgnoreFeature(String value){
        return StringUtils.isBlank(value) || (ignoreValues != null && ignoreValues.matcher(value).matches());
    }

    private Map<String, Double> castModelBuilderData(Object modelBuilderData) {
        if (modelBuilderData == null) {
            throw new IllegalArgumentException();
        }
        if (!(modelBuilderData instanceof Map)) {
            String errorMsg = "got illegal modelBuilderData type - probably bad ASL";
            logger.error(errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }
        return (Map<String, Double>) modelBuilderData;
    }
}
