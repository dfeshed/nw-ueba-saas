package fortscale.ml.model.builder;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.Assert;

@JsonAutoDetect(fieldVisibility = Visibility.NONE, getterVisibility = Visibility.ANY, setterVisibility = Visibility.ANY)
public class CategoryRarityModelBuilderConf implements IModelBuilderConf {
	public static final String CATEGORY_RARITY_MODEL_BUILDER = "category_rarity_model_builder";
	public static final int DEFAULT_ENTRIES_TO_SAVE_IN_MODEL = 100;
	public static final int DEFAULT_PARTITIONS_RESOLUTION_IN_SECONDS = 86400;

	private int numOfBuckets;
	@JsonProperty("entriesToSaveInModel")
	private int entriesToSaveInModel = DEFAULT_ENTRIES_TO_SAVE_IN_MODEL;
	@JsonProperty("partitionsResolutionInSeconds")
	private long partitionsResolutionInSeconds = DEFAULT_PARTITIONS_RESOLUTION_IN_SECONDS;

	@JsonCreator
	public CategoryRarityModelBuilderConf(@JsonProperty("numOfBuckets") int numOfBuckets) {
		setNumOfBuckets(numOfBuckets);
	}

	@JsonIgnore
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

	public void setPartitionsResolutionInSeconds(long partitionsResolutionInSeconds) {
		Assert.isTrue(partitionsResolutionInSeconds >= 0, "partitionsResolutionInSeconds must be a non-negative integer.");
		this.partitionsResolutionInSeconds = partitionsResolutionInSeconds;
	}

	public long getPartitionsResolutionInSeconds() {
		return partitionsResolutionInSeconds;
	}
}
