package fortscale.ml.scorer;

import fortscale.common.event.EventMessage;
import fortscale.common.feature.Feature;
import fortscale.common.feature.FeatureStringValue;
import fortscale.common.feature.extraction.FeatureExtractService;
import fortscale.ml.model.Model;
import fortscale.ml.model.cache.ModelsCacheService;

import fortscale.ml.scorer.config.ModelScorerConf;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.util.Assert;


import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Configurable(preConstruction = true)
public abstract class AbstractModelScorer extends AbstractScorer{

	protected String modelName;
	protected List<String> contextFieldNames;
	protected String featureName;
	protected int minNumOfSamplesToInfluence = ModelScorerConf.MIN_NUM_OF_SAMPLES_TO_INFLUENCE_DEFAULT_VALUE;
	protected int enoughNumOfSamplesToInfluence = ModelScorerConf.ENOUGH_NUM_OF_SAMPLES_TO_INFLUENCE_DEFAULT_VALUE;
	protected boolean isUseCertaintyToCalculateScore = ModelScorerConf.IS_USE_CERTAINTY_TO_CALCULATE_SCORE_DEAFEST_VALUE;
	protected ModelsCacheService modelsCacheService;

	@Autowired
	FeatureExtractService featureExtractService;


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

    /**
	 * This constructor is provided in order to use the scorer with model cache service, therefore the modelsCacheService
	 * cannot be null.
	 * @param scorerName
	 * @param modelName
	 * @param contextFieldNames
	 * @param featureName
	 * @param minNumOfSamplesToInfluence
	 * @param enoughNumOfSamplesToInfluence
	 * @param isUseCertaintyToCalculateScore
     * @param modelsCacheService
     */
	public AbstractModelScorer(String scorerName, String modelName,
									List<String> contextFieldNames,
									String featureName,
									int minNumOfSamplesToInfluence,
									int enoughNumOfSamplesToInfluence,
									boolean isUseCertaintyToCalculateScore,
									ModelsCacheService modelsCacheService){

		this(scorerName, featureName, minNumOfSamplesToInfluence, enoughNumOfSamplesToInfluence, isUseCertaintyToCalculateScore);

		//Assertions
		Assert.notNull(modelsCacheService);
		Assert.notEmpty(contextFieldNames);
		Assert.isTrue(StringUtils.isNotEmpty(modelName) && StringUtils.isNotBlank(modelName), "model name must be provided and cannot be empty or blank.");
		for(String contextFieldName: contextFieldNames) {
			Assert.isTrue(StringUtils.isNotEmpty(contextFieldName) && StringUtils.isNotBlank(contextFieldName), "context field name connot be null, empty or blank");
		}

		this.modelName = modelName;
		this.contextFieldNames = contextFieldNames;
		this.modelsCacheService = modelsCacheService;
	}

	/**
	 * This constructor is provided in order to be able to use the scorer without the model cache service.
	 * @param scorerName
	 * @param featureName
	 * @param minNumOfSamplesToInfluence
	 * @param enoughNumOfSamplesToInfluence
	 * @param isUseCertaintyToCalculateScore
     */
	public AbstractModelScorer(String scorerName,
							   String featureName,
							   int minNumOfSamplesToInfluence,
							   int enoughNumOfSamplesToInfluence,
							   boolean isUseCertaintyToCalculateScore){

		this(scorerName, featureName);
		assertMinNumOfSamplesToInfluenceValue(minNumOfSamplesToInfluence);
        assertEnoughNumOfSamplesToInfluence(enoughNumOfSamplesToInfluence);
		setMinNumOfSamplesToInfluence(minNumOfSamplesToInfluence);
        setEnoughNumOfSamplesToInfluence(enoughNumOfSamplesToInfluence);
        setUseCertaintyToCalculateScore(isUseCertaintyToCalculateScore);
	}

	/**
	 * This constructor is provided in order to be able to use the scorer without the model cache service.
	 * @param scorerName
	 * @param featureName
	 */
	public AbstractModelScorer(String scorerName, String featureName){
		super(scorerName);
		Assert.isTrue(StringUtils.isNotEmpty(featureName) && StringUtils.isNotBlank(featureName), "feature name cannot be null empty or blank");
		this.featureName = featureName;
		this.enoughNumOfSamplesToInfluence = Math.max(enoughNumOfSamplesToInfluence, minNumOfSamplesToInfluence);
	}

	protected Map<String, Feature> resolveContext(EventMessage eventMessage){
		Set<String> contextFields = new HashSet<>(contextFieldNames);
		Map<String, Feature> contextFieldNamesToValuesMap =  featureExtractService.extract(contextFields, eventMessage);
		
		return contextFieldNamesToValuesMap;
	}

	@Override
	public FeatureScore calculateScore(EventMessage eventMessage, long eventEpochTimeInSec) throws Exception {
		// get the context, so that we can get the model
		Map<String, Feature> contextFieldNamesToValuesMap = resolveContext(eventMessage);
		if (isNullOrMissingValues(contextFieldNamesToValuesMap)) {
			return new ModelFeatureScore(featureName, 0d, 0d);
		}

		Feature feature = featureExtractService.extract(featureName, eventMessage);
		Model model = modelsCacheService.getModel(feature, modelName, contextFieldNamesToValuesMap, eventEpochTimeInSec);

		return calculateScoreWithCertainty(model, feature);
		
	}

	public FeatureScore calculateScoreWithCertainty(Model model, Feature feature) {
		if(model == null || feature == null || feature.getValue() == null){
			return new ModelFeatureScore(featureName, 0d, 0d);
		}

		double score 		= calculateScore(model, feature);
		double certainty 	= calculateCertainty(model);

		if(isUseCertaintyToCalculateScore){
			return new FeatureScore(featureName, score*certainty);
		} else{
			return new ModelFeatureScore(featureName, score, certainty);
		}

	}

	abstract public double calculateScore(Model model, Feature feature);

	private boolean isNullOrMissingValues(Map<String, Feature> contextFieldNamesToValuesMap) {
		if(contextFieldNamesToValuesMap==null) {
			return false;
		}
		if(contextFieldNamesToValuesMap.values().size()!=contextFieldNames.size()) {
			return false;
		}
		for(Feature feature: contextFieldNamesToValuesMap.values()) {
			if(feature==null ||
				feature.getValue()==null ||
				((FeatureStringValue)feature.getValue()).getValue()==null ||
				StringUtils.isEmpty(((FeatureStringValue)feature.getValue()).getValue())) {
				return false;
			}
		}
		return true;
	}

	
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

}
