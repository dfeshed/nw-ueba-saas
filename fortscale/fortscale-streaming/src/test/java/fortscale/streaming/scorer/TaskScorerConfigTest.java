package fortscale.streaming.scorer;

import static com.google.common.base.Preconditions.checkNotNull;
import static fortscale.streaming.ConfigUtils.getConfigStringList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minidev.json.JSONObject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import fortscale.ml.model.prevalance.PrevalanceModel;
import fortscale.ml.service.ModelService;
import fortscale.streaming.TaskTestUtil;
import fortscale.streaming.feature.extractor.FeatureExtractionService;




public class TaskScorerConfigTest extends ScorerBaseTest{

	protected ModelService modelService;
	
	protected void whenModelServiceGetModel(String context, String modelName, PrevalanceModel model){
		try {
			when(modelService.getModel(context, modelName)).thenReturn(model);
		} catch (Exception e) {
			Assert.fail(e.toString());
		}
	}
	
	@Before
	public void setUp(){
		super.setUp();
		modelService = mock(ModelService.class);
	}
	
	@Test
	public void thisAlwaysPasses() {
	}

	@Test
	@Ignore
	public void thisIsIgnored() {
	}

	
	protected Map<String, Scorer> buildScorersFromTaskConfig(String taskConfigPropertiesFilePath) throws IOException{
		config = TaskTestUtil.buildTaskConfig(taskConfigPropertiesFilePath);

		List<String> scorers = getConfigStringList(config, "fortscale.scorers");
		context = new ScorerContext(config);
		context.setBean("modelService", modelService);
		featureExtractionService = new FeatureExtractionService(config);
		context.setBean("featureExtractionService", featureExtractionService);
		Map<String, Scorer> scorersToRun = new HashMap<>();
		for(String ScorerStr: scorers){
			Scorer scorer = (Scorer) context.resolve(Scorer.class, ScorerStr);
			checkNotNull(scorer);
			scorersToRun.put(ScorerStr, scorer);
		}
		return scorersToRun;
	}
	
	protected EventMessage buildEventMessage(boolean isAddInput, String fieldName, Object fieldValue){
		JSONObject jsonObject = null;
		if(isAddInput){
			jsonObject = new JSONObject();
			jsonObject.put(fieldName, fieldValue);
		}
		
		return new EventMessage(jsonObject);
	}
	
	protected void addToEventMessage(EventMessage eventMessage, String fieldName, Object fieldValue){
		JSONObject jsonObject = eventMessage.getJsonObject();
		jsonObject.put(fieldName, fieldValue);
	}
}
