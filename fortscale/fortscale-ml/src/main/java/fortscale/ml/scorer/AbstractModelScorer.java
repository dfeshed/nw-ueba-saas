package fortscale.ml.scorer;

import fortscale.common.event.Event;
import fortscale.common.feature.Feature;
import fortscale.common.feature.extraction.FeatureExtractService;
import fortscale.domain.feature.score.FeatureScore;
import fortscale.domain.feature.score.ModelFeatureScore;
import fortscale.ml.model.Model;
import fortscale.ml.model.cache.EventModelsCacheService;
import fortscale.ml.scorer.config.ModelScorerConf;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class AbstractModelScorer extends AbstractScorer{
	private String modelName;
	private List<String> additionalModelNames;
	private List<String> contextFieldNames;
	private List<List<String>> additionalContextFieldNames;
	private String featureName;
	private int minNumOfSamplesToInfluence = ModelScorerConf.MIN_NUM_OF_SAMPLES_TO_INFLUENCE_DEFAULT_VALUE;
	private int enoughNumOfSamplesToInfluence = ModelScorerConf.ENOUGH_NUM_OF_SAMPLES_TO_INFLUENCE_DEFAULT_VALUE;
	private boolean isUseCertaintyToCalculateScore = ModelScorerConf.IS_USE_CERTAINTY_TO_CALCULATE_SCORE_DEFAULT_VALUE;


	protected final EventModelsCacheService eventModelsCacheService;
	protected final FeatureExtractService featureExtractService;

    static public void assertMinNumOfSamplesToInfluenceValue(int minNumOfSamplesToInfluence) {
        Assert.isTrue(minNumOfSamplesToInfluence >= 1, String.format("minNumOfSamplesToInfluence (%d) must be >= 1", minNumOfSamplesToInfluence));
    }
    static public void assertEnoughNumOfSamplesToInfluence(int enoughNumOfSamplesToInfluence) {
        Assert.isTrue(enoughNumOfSamplesToInfluence >= 1, String.format("enoughNumOfSamplesToInfluence (%d) must be >=1", enoughNumOfSamplesToInfluence));
    }

    public AbstractModelScorer setMinNumOfSamplesToInfluence(int minNumOfSamplesToInfluence) {
        assertMinNumOfSamplesToInfluenceValue(minNumOfSamplesToInfluence);
        this.minNumOfSamplesToInfluence = minNumOfSamplesToInfluence;
        return this;
    }

    public AbstractModelScorer setEnoughNumOfSamplesToInfluence(int enoughNumOfSamplesToInfluence) {
        this.enoughNumOfSamplesToInfluence = Math.max(enoughNumOfSamplesToInfluence, minNumOfSamplesToInfluence);
        return this;
    }

    public AbstractModelScorer setUseCertaintyToCalculateScore(boolean useCertaintyToCalculateScore) {
        isUseCertaintyToCalculateScore = useCertaintyToCalculateScore;
        return this;
    }

	public AbstractModelScorer(String scorerName,
							   String modelName,
							   List<String> additionalModelNames,
							   List<String> contextFieldNames,
							   List<List<String>> additionalContextFieldNames,
							   String featureName,
							   int minNumOfSamplesToInfluence,
							   int enoughNumOfSamplesToInfluence,
							   boolean isUseCertaintyToCalculateScore, FeatureExtractService featureExtractService,
							   EventModelsCacheService eventModelsCacheService){
		super(scorerName);
		this.eventModelsCacheService = eventModelsCacheService;
		Assert.isTrue(StringUtils.isNotBlank(featureName), "feature name cannot be null empty or blank");
		this.featureName = featureName;
		this.enoughNumOfSamplesToInfluence = Math.max(enoughNumOfSamplesToInfluence, minNumOfSamplesToInfluence);
		assertMinNumOfSamplesToInfluenceValue(minNumOfSamplesToInfluence);
		assertEnoughNumOfSamplesToInfluence(enoughNumOfSamplesToInfluence);
		setMinNumOfSamplesToInfluence(minNumOfSamplesToInfluence);
		setEnoughNumOfSamplesToInfluence(enoughNumOfSamplesToInfluence);
		setUseCertaintyToCalculateScore(isUseCertaintyToCalculateScore);

		//Assertions
		if (additionalModelNames == null) {
			additionalModelNames = Collections.emptyList();
		}
		if (additionalContextFieldNames == null) {
			additionalContextFieldNames = Collections.emptyList();
		}
		Assert.notEmpty(contextFieldNames);
		Assert.isTrue(StringUtils.isNotBlank(modelName), "model name must be provided and cannot be empty or blank.");
		Assert.isTrue(additionalModelNames.size() == additionalContextFieldNames.size(), "additionalModelNames and additionalContextFieldNames must have the same size");
		for (String additionalModelName : additionalModelNames) {
			Assert.isTrue(StringUtils.isNotBlank(additionalModelName), "additional model names cannot be empty or blank.");
		}
		for (String contextFieldName : contextFieldNames) {
			Assert.isTrue(StringUtils.isNotBlank(contextFieldName), "context field name cannot be null, empty or blank.");
		}
		for (List<String> c : additionalContextFieldNames) {
			for (String contextFieldName : c) {
				Assert.isTrue(StringUtils.isNotBlank(contextFieldName), "context field name cannot be null, empty or blank.");
			}
		}

		this.modelName = modelName;
		this.additionalModelNames = additionalModelNames;
		this.contextFieldNames = contextFieldNames;
		this.additionalContextFieldNames = additionalContextFieldNames;
		this.featureExtractService = featureExtractService;
	}

	@Override
	public FeatureScore calculateScore(Event eventMessage, long eventEpochTimeInSec) throws Exception {
		Feature feature = featureExtractService.extract(featureName, eventMessage);
		List<Model> additionalModels = IntStream.range(0, additionalModelNames.size())
				.mapToObj(i -> eventModelsCacheService.getModel(
						eventMessage,
						feature,
						eventEpochTimeInSec,
						additionalModelNames.get(i),
						additionalContextFieldNames.get(i))
				).collect(Collectors.toList());
		return calculateScoreWithCertainty(
				getModel(eventMessage, eventEpochTimeInSec, feature),
				additionalModels,
				feature);
	}

	Model getModel(Event eventMessage, long eventEpochTimeInSec) {
		Feature feature = featureExtractService.extract(featureName, eventMessage);
		return getModel(eventMessage, eventEpochTimeInSec, feature);
	}

	private Model getModel(Event eventMessage, long eventEpochTimeInSec, Feature feature) {
		return eventModelsCacheService.getModel(eventMessage, feature, eventEpochTimeInSec, modelName, contextFieldNames);
	}

	public FeatureScore calculateScoreWithCertainty(Model model, List<Model> additionalModels, Feature feature) {
		if(model == null || additionalModels.contains(null) || feature == null || feature.getValue() == null){
			return new ModelFeatureScore(getName(), 0d, 0d);
		}

		double score 		= calculateScore(model, additionalModels, feature);
		double certainty 	= calculateCertainty(model);

		if(isUseCertaintyToCalculateScore){
			return new FeatureScore(getName(), score*certainty);
		} else{
			return new ModelFeatureScore(getName(), score, certainty);
		}

	}

	abstract protected double calculateScore(Model model, List<Model> additionalModels, Feature feature);
	
	protected double calculateCertainty(Model model){
		if(enoughNumOfSamplesToInfluence<=1){
			return 1;
		}
		
		long numOfSamples = model.getNumOfSamples();
		double certainty = 0;
		if(numOfSamples >= enoughNumOfSamplesToInfluence){
			certainty = 1;
		} else if(numOfSamples >= minNumOfSamplesToInfluence){
			certainty = ((double)(numOfSamples - minNumOfSamplesToInfluence + 1)) / (enoughNumOfSamplesToInfluence - minNumOfSamplesToInfluence + 1);
		}
		return certainty;
	}

	public String getModelName() {
		return modelName;
	}

	public List<String> getContextFieldNames() {
		return contextFieldNames;
	}

	public String getFeatureName() {
		return featureName;
	}

	public int getMinNumOfSamplesToInfluence() {
		return minNumOfSamplesToInfluence;
	}

	public int getEnoughNumOfSamplesToInfluence() {
		return enoughNumOfSamplesToInfluence;
	}

	public boolean isUseCertaintyToCalculateScore() {
		return isUseCertaintyToCalculateScore;
	}
}
