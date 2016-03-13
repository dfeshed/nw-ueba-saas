package fortscale.ml.model.builder;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.Assert;

@JsonAutoDetect(fieldVisibility = Visibility.NONE, getterVisibility = Visibility.ANY, setterVisibility = Visibility.ANY)
public class CategoryRarityModelBuilderConf implements IModelBuilderConf {
	public static final String CATEGORY_RARITY_MODEL_BUILDER = "category_rarity_model_builder";
	public static final int DEFAULT_ENTRIES_TO_SAVE_IN_MODEL = 100;

	private int numOfBuckets;
	@JsonProperty("entriesToSaveInModel")
	private int entriesToSaveInModel = DEFAULT_ENTRIES_TO_SAVE_IN_MODEL;

	@JsonCreator
	public CategoryRarityModelBuilderConf(@JsonProperty("numOfBuckets") int numOfBuckets) {
		setNumOfBuckets(numOfBuckets);
	}

	@Override
	public String getFactoryName() {
		return CATEGORY_RARITY_MODEL_BUILDER;
	}

	public int getNumOfBuckets() {
		return numOfBuckets;
	}

	public void setNumOfBuckets(int numOfBuckets) {
		Assert.isTrue(numOfBuckets > 0, "numOfBuckets is mandatory and must be a positive integer.");
		this.numOfBuckets = numOfBuckets;
	}

	public int getEntriesToSaveInModel() {
		return entriesToSaveInModel;
	}

	public void setEntriesToSaveInModel(int entriesToSaveInModel) {
		Assert.isTrue(entriesToSaveInModel >= 0, "entriesToSaveInModel must be a non-negative integer.");
		this.entriesToSaveInModel = entriesToSaveInModel;
	}
}
