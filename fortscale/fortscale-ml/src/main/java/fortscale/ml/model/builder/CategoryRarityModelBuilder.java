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
    private int maxRareCount;
    private int maxNumOfRareFeatures;

    @JsonCreator
    public CategoryRarityModelBuilder(@JsonProperty("ignorePattern") String ignorePattern,
                                      @JsonProperty("maxRareCount") Integer maxRareCount,
                                      @JsonProperty("maxNumOfRareFeatures") Integer maxNumOfRareFeatures) {
        Assert.notNull(maxRareCount);
        Assert.isTrue(maxRareCount > 0);
        Assert.notNull(maxNumOfRareFeatures);
        Assert.isTrue(maxNumOfRareFeatures > 0);
        this.maxRareCount = maxRareCount;
        this.maxNumOfRareFeatures = maxNumOfRareFeatures;
        if (ignorePattern != null) {
            ignoreValues = Pattern.compile(ignorePattern);
        }
    }

    @Override
    public Model build(Object modelBuilderData) {
        Map<String, Integer> featureValueToCountMap = castModelBuilderData(modelBuilderData);
        return new CategoryRarityModel(getUnignoredCounts(featureValueToCountMap), maxRareCount, maxNumOfRareFeatures);
    }

    @Override
    public double calculateScore(Object value, Model model) {
        Pair<String, Double> featureAndCount = (Pair<String, Double>) value;
        return shouldIgnoreFeature(featureAndCount.getKey()) ? 0 : model.calculateScore(featureAndCount.getValue());
    }

    private Collection<Integer> getUnignoredCounts(Map<String, Integer> featureValueToCountMap) {
        List<Integer> counts = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : featureValueToCountMap.entrySet()) {
            if (!shouldIgnoreFeature(entry.getKey())) {
                counts.add(entry.getValue());
            }
        }
        return counts;
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
