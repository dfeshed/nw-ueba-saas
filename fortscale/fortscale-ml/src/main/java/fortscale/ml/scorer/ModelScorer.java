package fortscale.ml.scorer;

import fortscale.common.event.EventMessage;
import fortscale.ml.model.ModelConf;
import fortscale.ml.model.ModelConfService;
import fortscale.ml.model.cache.ModelsCacheService;
import fortscale.ml.model.prevalance.FieldModel;
import fortscale.ml.model.prevalance.PrevalanceModel;
import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.model.retriever.AbstractDataRetrieverConf;
import fortscale.ml.scorer.config.ModelInfo;
import fortscale.utils.factory.FactoryService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public class ModelScorer extends AbstractScorer{
/*
	public static final boolean DEFAULT_USE_CERTAINTY_TO_CALCULATE_SCORE = false;
	
	protected String modelName;
	protected String contextFieldName;
	protected String optionalContextReplacementFieldName;
	protected String featureFieldName;
	private int minNumOfSamplesToInfluence;
	private int enoughNumOfSamplesToInfluence;
	private boolean isUseCertaintyToCalculateScore = false;
	protected ModelsCacheService modelsCacheService;

	@Autowired
	FactoryService<AbstractDataRetriever> dataRetrieverFactoryService;

	@Autowired
	protected ModelConfService modelConfService;

	public ModelScorer(String scorerName, ModelInfo modelInfo,
					   int minNumOfSamplesToInfluence,
					   int enoughNumOfSamplesToInfluence,
					   boolean isUseCertaintyToCalculateScore,
					   ModelsCacheService modelsCacheService){
		super(scorerName);
		checkNotNull(modelsCacheService);
		modelName = modelInfo.getModelName();
		ModelConf modelConf = modelConfService.getModelConf(modelName);
		AbstractDataRetrieverConf dataRetrieverConf = modelConf.getDataRetrieverConf();
		AbstractDataRetriever abstractDataRetriever = dataRetrieverFactoryService.getProduct(dataRetrieverConf);
		List<String> contextFieldNames = abstractDataRetriever.getContextFieldNames();
		Set<String> featureNames =  abstractDataRetriever.getEventFeatureNames();

		featureFieldName = getConfigString(config, String.format("fortscale.score.%s.%s.fieldname", scorerName, modelName));
		contextFieldName = getConfigString(config, String.format("fortscale.score.%s.%s.context.fieldname", scorerName, modelName));
		optionalContextReplacementFieldName = config.get(String.format("fortscale.score.%s.%s.context.fieldname.optional.replacement", scorerName, modelName));
		this.minNumOfSamplesToInfluence = minNumOfSamplesToInfluence;
		this.enoughNumOfSamplesToInfluence = Math.max(enoughNumOfSamplesToInfluence, minNumOfSamplesToInfluence);
		this.isUseCertaintyToCalculateScore = isUseCertaintyToCalculateScore;
		this.modelsCacheService = modelsCacheService;
	}
	
	protected String resolveContext(EventMessage eventMessage){
		String context = (String) featureExtractionService.extract(contextFieldName, eventMessage.getJsonObject());
		if(StringUtils.isBlank(context) && optionalContextReplacementFieldName != null){
			context = (String) featureExtractionService.extract(optionalContextReplacementFieldName, eventMessage.getJsonObject());
		}
		
		return context;
	}

	@Override
	public FeatureScore calculateScore(EventMessage eventMessage) throws Exception {
		// get the context, so that we can get the model
		String context = resolveContext(eventMessage);
		if (StringUtils.isEmpty(context)) {
			return new ModelFeatureScore(outputFieldName, 0d, 0d);
		}
		
		PrevalanceModel model = modelService.getModel(context, modelName);
		if(model == null){
			return new ModelFeatureScore(outputFieldName, 0d, 0d);
		}
		double score = model.calculateScore(featureExtractionService, eventMessage.getJsonObject(), featureFieldName);
		score = calibrateScore(score);
		
		double certainty = calculateCertainty(model);
				
		if(isUseCertaintyToCalculateScore){
			return new FeatureScore(outputFieldName, score*certainty);
		} else{
			return new ModelFeatureScore(outputFieldName, score, certainty);
		}
		
		
	}
	
	protected double calibrateScore(double score){
		return score;
	}
	
	protected double calculateCertainty(PrevalanceModel model){
		if(enoughNumOfSamplesToInfluence<=1){
			return 1;
		}
		
		FieldModel fieldModel = model.getFieldModel(featureFieldName);
		long numOfSamples = fieldModel.getNumOfSamples();
		double certainty = 0;
		if(numOfSamples >= enoughNumOfSamplesToInfluence){
			certainty = 1;
		} else if(numOfSamples >= minNumOfSamplesToInfluence){
			certainty = ((double)(numOfSamples - minNumOfSamplesToInfluence + 1)) / (enoughNumOfSamplesToInfluence - minNumOfSamplesToInfluence + 1);
		}
		return certainty;
	}
	*/

	public ModelScorer(String name) {
		super(name);
	}

	@Override
	public FeatureScore calculateScore(EventMessage eventMessage) throws Exception {
		return null;
	}
}
