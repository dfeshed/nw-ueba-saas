package fortscale.ml.model.builder;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.Assert;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY)
public class CategoryRarityGlobalModelBuilderConf implements IModelBuilderConf{
    public static final String CATEGORY_RARITY_GLOBAL_MODEL_BUILDER = "category_rarity_global_model_builder";

    private static final Integer MIN_NUM_OF_PARTITIONS_TO_LEARN_FROM_DEFAULT_VALUE = 10;

    private int minNumOfPartitionsToLearnFrom;

    @JsonCreator
    public CategoryRarityGlobalModelBuilderConf(@JsonProperty("minNumOfPartitionsToLearnFrom") Integer minNumOfPartitionsToLearnFrom){
        setMinNumOfPartitionsToLearnFrom(minNumOfPartitionsToLearnFrom);
    }




    @Override
    public String getFactoryName() {
        return CATEGORY_RARITY_GLOBAL_MODEL_BUILDER;
    }


    public int getMinNumOfPartitionsToLearnFrom() {
        return minNumOfPartitionsToLearnFrom;
    }

    public void setMinNumOfPartitionsToLearnFrom(Integer minNumOfPartitionsToLearnFrom) {
        if (minNumOfPartitionsToLearnFrom == null) {
            minNumOfPartitionsToLearnFrom = MIN_NUM_OF_PARTITIONS_TO_LEARN_FROM_DEFAULT_VALUE;
        }
        Assert.isTrue(minNumOfPartitionsToLearnFrom >= 0, "minNumOfPartitionsToLearnFrom should be > 0.");
        this.minNumOfPartitionsToLearnFrom = minNumOfPartitionsToLearnFrom;
    }
}
