package fortscale.streaming.scorer;

import static com.google.common.base.Preconditions.checkNotNull;
import static fortscale.streaming.ConfigUtils.getConfigString;

import org.apache.commons.lang.StringUtils;
import org.apache.samza.config.Config;

import fortscale.ml.model.prevalance.FieldModel;
import fortscale.ml.model.prevalance.PrevalanceModel;
import fortscale.ml.service.ModelService;

public class ModelScorer extends AbstractScorer{
//	private static final Logger logger = Logger.getLogger(EventFeatureScorer.class);
	
 static final String IS_USE_CERTAINTY_TO_CALCULATE_CONFIG_FORMAT = "fortscale.score.%s.continuous.model.large.value";
	public static final boolean DEFAULT_USE_CERTAINTY_TO_CALCULATE_SCORE = false;
	
	protected ModelService modelService;
	protected String modelName;
	protected String contextFieldName;
	protected String optionalContextReplacementFieldName;
	protected String featureFieldName;
	private int minNumOfSamplesToInfluence;
	private int enoughNumOfSamplesToInfluence;
	private boolean isUseCertaintyToCalculateScore = false;

	public ModelScorer(String scorerName, Config config, ScorerContext context){
		super(scorerName,config, context);
		modelName = getConfigString(config, String.format("fortscale.score.%s.model.name", scorerName));
		featureFieldName = getConfigString(config, String.format("fortscale.score.%s.%s.fieldname", scorerName, modelName));
		contextFieldName = getConfigString(config, String.format("fortscale.score.%s.%s.context.fieldname", scorerName, modelName));
		optionalContextReplacementFieldName = config.get(String.format("fortscale.score.%s.%s.context.fieldname.optional.replacement", scorerName, modelName));
		minNumOfSamplesToInfluence = config.getInt(String.format("fortscale.score.%s.num.of.samples.to.influence.min", scorerName), 1);
		enoughNumOfSamplesToInfluence = Math.max(config.getInt(String.format("fortscale.score.%s.num.of.samples.to.influence.enough", scorerName), 1), minNumOfSamplesToInfluence);
		isUseCertaintyToCalculateScore = config.getBoolean(String.format("fortscale.score.%s.use.certainty.to.calculate.score", scorerName), DEFAULT_USE_CERTAINTY_TO_CALCULATE_SCORE);
		
		this.modelService = (ModelService) context.resolve(ModelService.class, "modelService");
		checkNotNull(modelService);
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
}
