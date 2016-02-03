package fortscale.ml.model.builder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.Assert;

public class CategoryRarityModelBuilderConf implements IModelBuilderConf {
	public static final String CATEGORY_RARITY_MODEL_BUILDER = "category_rarity_model_builder";

	private int numOfBuckets;

	@JsonCreator
	public CategoryRarityModelBuilderConf(@JsonProperty("numOfBuckets") int numOfBuckets) {
		Assert.isTrue(numOfBuckets > 0);
		this.numOfBuckets = numOfBuckets;
	}

	@Override
	public String getFactoryName() {
		return CATEGORY_RARITY_MODEL_BUILDER;
	}

	public int getNumOfBuckets() {
		return numOfBuckets;
	}
}
