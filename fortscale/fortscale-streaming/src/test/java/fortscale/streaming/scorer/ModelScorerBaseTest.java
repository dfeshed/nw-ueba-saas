package fortscale.streaming.scorer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Before;

import fortscale.ml.model.prevalance.PrevalanceModel;
import fortscale.ml.service.ModelService;

public class ModelScorerBaseTest extends ScorerBaseTest{
	protected static final String FIELD_NAME = "testFieldName";
	protected static final String FIELD_VALUE = "testFieldValue";
	protected static final String OUTPUT_FIELD_NAME = "outputTestField";
	protected static final String SCORER_NAME = "ModelScorerTestScorerName";
	protected static final String MODEL_NAME = "testModelName";
	protected static final String CONTEXT_NAME = "testContextName";
	protected static final String CONTEXT = "testuser";
	protected static final String OPTIONAL_CONTEXT_NAME = "optionalTestContextName";
	protected static final String OPTIONAL_CONTEXT = "optionTestip";
	
	protected ScorerContext context;
	protected ModelService modelService;
	protected PrevalanceModel model;
	protected PrevalanceModel optionalModel;
	
	@Before
	public void setUp(){
		super.setUp();
		context = new ScorerContext(config);
		modelService = mock(ModelService.class);
		model = mock(PrevalanceModel.class);
		optionalModel = mock(PrevalanceModel.class);
		try {
			when(modelService.getModel(CONTEXT, MODEL_NAME)).thenReturn(model);
			when(modelService.getModel(OPTIONAL_CONTEXT, MODEL_NAME)).thenReturn(optionalModel);
		} catch (Exception e) {
			Assert.fail(e.toString());
		}
		context.setBean("modelService", modelService);
	}
	
	protected void prepareConfig(String scorerType, String scorerName, String outputFieldName, String modelName, String fieldName, String contextName, String optionalContextReplacementFieldName){
		if(scorerName !=null){
			when(config.get(String.format("fortscale.score.%s.scorer", scorerName))).thenReturn(scorerType);
			if(modelName != null){
				when(config.get(String.format("fortscale.score.%s.model.name", scorerName))).thenReturn(modelName);
				if(fieldName != null){
					when(config.get(String.format("fortscale.score.%s.%s.fieldname", scorerName, modelName))).thenReturn(fieldName);
				}
				if(contextName != null){
					when(config.get(String.format("fortscale.score.%s.%s.context.fieldname", scorerName, modelName))).thenReturn(contextName);
				}
				if(optionalContextReplacementFieldName != null){
					when(config.get(String.format("fortscale.score.%s.%s.context.fieldname.optional.replacement", scorerName, modelName))).thenReturn(optionalContextReplacementFieldName);
				}
			}
			if(outputFieldName != null)
				when(config.get(String.format("fortscale.score.%s.output.field.name", scorerName))).thenReturn(outputFieldName);
		}
	}
	
	
}
