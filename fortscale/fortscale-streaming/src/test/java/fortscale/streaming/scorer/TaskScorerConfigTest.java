package fortscale.streaming.scorer;

import static com.google.common.base.Preconditions.checkNotNull;
import static fortscale.streaming.ConfigUtils.getConfigStringList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.samza.config.Config;
import org.apache.samza.config.MapConfig;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import fortscale.ml.model.prevalance.PrevalanceModel;
import fortscale.ml.service.ModelService;




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
	
	protected Config buildTaskConfig(String taskConfigPropertiesFilePath) throws IOException{
		final Properties properties = new Properties();
		FileInputStream fileInputStream = new FileInputStream(new File(taskConfigPropertiesFilePath));
		
		properties.load(fileInputStream);
		
		Map<String,String> propMap = new HashMap<>();
		for(Object key: properties.keySet()){
			String keyStr = (String) key;
			propMap.put(keyStr, properties.getProperty(keyStr));
		}

		return new MapConfig(propMap);
	}

	
	protected List<Scorer> buildScorersFromTaskConfig(String taskConfigPropertiesFilePath) throws IOException{
		Config config = buildTaskConfig(taskConfigPropertiesFilePath);

		List<String> scorers = getConfigStringList(config, "fortscale.scorers");
		ScorerContext context = new ScorerContext(config);
		context.setBean("modelService", modelService);
		List<Scorer> scorersToRun = new ArrayList<>();
		for(String ScorerStr: scorers){
			Scorer scorer = (Scorer) context.resolve(Scorer.class, ScorerStr);
			checkNotNull(scorer);
			scorersToRun.add(scorer);
		}
		return scorersToRun;
	}
}
