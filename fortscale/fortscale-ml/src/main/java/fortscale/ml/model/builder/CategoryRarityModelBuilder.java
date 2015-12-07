package fortscale.ml.model.builder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fortscale.ml.model.Model;
import fortscale.ml.model.prevalance.field.CategoryRarityModel;
import fortscale.utils.logging.Logger;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.util.Assert;

import java.util.*;
import java.util.regex.Pattern;

public class CategoryRarityModelBuilder implements IModelBuilder {
    private static final Logger logger = Logger.getLogger(CategoryRarityModelBuilder.class);
    public static final String MODEL_BUILDER_TYPE = "category_rarity";

    private Pattern ignoreValues;
    private int minEvents;
    private int maxRareCount;
    private int maxNumOfRareFeatures;

    @JsonCreator
    public CategoryRarityModelBuilder(@JsonProperty("ignorePattern") String ignorePattern,
                                      @JsonProperty("minEvents") Integer minEvents,
                                      @JsonProperty("maxRareCount") Integer maxRareCount,
                                      @JsonProperty("maxNumOfRareFeatures") Integer maxNumOfRareFeatures) {
        Assert.notNull(minEvents);
        Assert.isTrue(minEvents > 0);
        Assert.notNull(maxRareCount);
        Assert.isTrue(maxRareCount > 0);
        Assert.notNull(maxNumOfRareFeatures);
        Assert.isTrue(maxNumOfRareFeatures > 0);
        this.minEvents = minEvents;
        this.maxRareCount = maxRareCount;
        this.maxNumOfRareFeatures = maxNumOfRareFeatures;
        if (ignorePattern != null) {
            ignoreValues = Pattern.compile(ignorePattern);
        }
    }

    @Override
    public Model build(Object modelBuilderData) {
        Map<String, Integer> featureValueToCountMap = castModelBuilderData(modelBuilderData);
        return new CategoryRarityModel(minEvents, maxRareCount, maxNumOfRareFeatures, getUnignoredOccurrencesToNumOfFeatures(featureValueToCountMap));
    }

    @Override
    public double calculateScore(Object value, Model model) {
        Pair<String, Double> featureAndCount = (Pair<String, Double>) value;
        return shouldIgnoreFeature(featureAndCount.getKey()) ? 0 : model.calculateScore(featureAndCount.getValue());
    }

    private Map<Integer, Double> getUnignoredOccurrencesToNumOfFeatures(Map<String, Integer> featureValueToCountMap) {
        Map<Integer, Double> occurrencesToNumOfFeatures = new HashMap<>();
        for (Map.Entry<String, Integer> entry : featureValueToCountMap.entrySet()) {
            if (!shouldIgnoreFeature(entry.getKey())) {
                int count = entry.getValue();
                Double numOfFeatures = occurrencesToNumOfFeatures.get(count);
                if (numOfFeatures == null) {
                    numOfFeatures = 0D;
                }
                occurrencesToNumOfFeatures.put(count, numOfFeatures + 1);
            }
        }
        return occurrencesToNumOfFeatures;
    }

    private boolean shouldIgnoreFeature(String value){
        return StringUtils.isBlank(value) || (ignoreValues != null && ignoreValues.matcher(value).matches());
    }

    private Map<String, Integer> castModelBuilderData(Object modelBuilderData) {
        if (modelBuilderData == null) {
            throw new IllegalArgumentException();
        }
        if (!(modelBuilderData instanceof Map)) {
            String errorMsg = "got illegal modelBuilderData type - probably bad ASL";
            logger.error(errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }
        return (Map<String, Integer>) modelBuilderData;
    }
}
