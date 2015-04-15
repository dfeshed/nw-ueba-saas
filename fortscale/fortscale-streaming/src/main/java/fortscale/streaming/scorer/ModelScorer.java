package fortscale.streaming.scorer;

import static com.google.common.base.Preconditions.checkNotNull;
import static fortscale.streaming.ConfigUtils.getConfigString;

import org.apache.commons.lang.StringUtils;
import org.apache.samza.config.Config;

import fortscale.ml.model.prevalance.PrevalanceModel;
import fortscale.ml.service.ModelService;

public class ModelScorer extends AbstractScorer{
//	private static final Logger logger = Logger.getLogger(EventFeatureScorer.class);
	
	
	protected ModelService modelService;
	protected String modelName;
	protected String contextFieldName;
	protected String optionalContextReplacementFieldName;
	protected String featureFieldName;

	public ModelScorer(String scorerName, Config config, ScorerContext context){
		super(scorerName,config, context);
		modelName = getConfigString(config, String.format("fortscale.score.%s.model.name", scorerName));
		featureFieldName = getConfigString(config, String.format("fortscale.score.%s.%s.fieldname", scorerName, modelName));
		contextFieldName = getConfigString(config, String.format("fortscale.score.%s.%s.context.fieldname", scorerName, modelName));
		optionalContextReplacementFieldName = config.get(String.format("fortscale.score.%s.%s.context.fieldname.optional.replacement", scorerName, modelName));
		
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
		
		return calculateModelScore(eventMessage, model);
		
		
	}
	
	protected FeatureScore calculateModelScore(EventMessage eventMessage, PrevalanceModel model) throws Exception{
		double score = 0;
		if(model != null){
			score = model.calculateScore(featureExtractionService, eventMessage.getJsonObject(), featureFieldName);
		}
		
		return new FeatureScore(outputFieldName, score);
	}
}
