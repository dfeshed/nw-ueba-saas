package fortscale.streaming.scorer;

import static com.google.common.base.Preconditions.checkNotNull;
import static fortscale.streaming.ConfigUtils.getConfigString;

import org.apache.commons.lang.StringUtils;
import org.apache.samza.config.Config;

import fortscale.ml.model.prevalance.PrevalanceModel;
import fortscale.ml.service.ModelService;
import fortscale.streaming.exceptions.StreamMessageNotContainFieldException;

public class ModelScorer extends AbstractScorer{
//	private static final Logger logger = Logger.getLogger(EventFeatureScorer.class);
	
	
	private ModelService modelService;
	private String modelName;
	private String contextFieldName;
	private String featureFieldName;

	public ModelScorer(String scorerName, Config config, ScorerContext context){
		super(scorerName,config);
		modelName = getConfigString(config, String.format("fortscale.score.%s.model.name", scorerName));
		featureFieldName = getConfigString(config, String.format("fortscale.score.%s.%s.fieldname", scorerName, modelName));
		contextFieldName = getConfigString(config, String.format("fortscale.score.%s.%s.context.fieldname", scorerName, modelName));
		this.modelService = (ModelService) context.resolve(ModelService.class, "modelService");
		checkNotNull(modelService);
	}

	@Override
	public Double calculateScore(EventMessage eventMessage) throws Exception {
		// get the context, so that we can get the model
		String context = eventMessage.getEventStringValue(contextFieldName);
		if (StringUtils.isEmpty(context)) {
			throw new StreamMessageNotContainFieldException(eventMessage.toJSONString(), contextFieldName);
		}
		
		// go over each field in the event and add it to the model
		PrevalanceModel model = modelService.getModel(context, modelName);
		
		double score = 0;
		if(model != null){
			score = model.calculateScore(eventMessage.getJsonObject(), featureFieldName);
		}
		
		eventMessage.setScore(outputFieldName, score);
		
		return score;
	}
}
