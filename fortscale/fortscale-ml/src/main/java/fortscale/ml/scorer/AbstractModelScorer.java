package fortscale.ml.scorer;

import fortscale.common.event.EventMessage;
import fortscale.common.feature.Feature;
import fortscale.common.feature.FeatureStringValue;
import fortscale.common.feature.extraction.FeatureExtractService;
import fortscale.ml.model.Model;
import fortscale.ml.model.cache.ModelsCacheService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public abstract class AbstractModelScorer extends AbstractScorer{

	public static final boolean DEFAULT_USE_CERTAINTY_TO_CALCULATE_SCORE = false;
	
	protected String modelName;
	protected List<String> contextFieldNames;
	protected String featureName;
	protected int minNumOfSamplesToInfluence;
	protected int enoughNumOfSamplesToInfluence;
	protected boolean isUseCertaintyToCalculateScore = false;
	protected ModelsCacheService modelsCacheService;

	@Autowired
	FeatureExtractService featureExtractService;

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
		super(scorerName);
		Assert.notNull(modelsCacheService);
		Assert.notEmpty(contextFieldNames);
		this.modelName = modelName;
		this.contextFieldNames = contextFieldNames;
		this.featureName = featureName;
		this.minNumOfSamplesToInfluence = minNumOfSamplesToInfluence;
		this.enoughNumOfSamplesToInfluence = Math.max(enoughNumOfSamplesToInfluence, minNumOfSamplesToInfluence);
		this.isUseCertaintyToCalculateScore = isUseCertaintyToCalculateScore;
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

		super(scorerName);
		this.featureName = featureName;
		this.minNumOfSamplesToInfluence = minNumOfSamplesToInfluence;
		this.enoughNumOfSamplesToInfluence = Math.max(enoughNumOfSamplesToInfluence, minNumOfSamplesToInfluence);
		this.isUseCertaintyToCalculateScore = isUseCertaintyToCalculateScore;
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
		
		Model model = modelsCacheService.getModel(feature, contextFieldNamesToValuesMap, modelName, eventEpochTimeInSec);

		return calculateScoreWithCertainty(model, feature);
		
	}

	public FeatureScore calculateScoreWithCertainty(Model model, Feature feature) {
		if(model == null){
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
