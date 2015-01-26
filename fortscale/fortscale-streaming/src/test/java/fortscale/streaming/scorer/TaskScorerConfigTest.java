package fortscale.streaming.scorer;

import static com.google.common.base.Preconditions.checkNotNull;
import static fortscale.streaming.ConfigUtils.getConfigStringList;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.samza.config.MapConfig;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fortscale.ml.service.ModelService;




@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:META-INF/spring/scorers-context-test.xml" })
public class TaskScorerConfigTest {

private ModelService modelService;
	
	@Before
	public void setUp(){
		modelService = mock(ModelService.class);
	}
	
	@Test
	public void thisAlwaysPasses() {
	}

	@Test
	@Ignore
	public void thisIsIgnored() {
	}
	
	
	
	protected void testSanity(String taskConfigPropertiesFilePath) throws IOException{
		final Properties properties = new Properties();
//		InputStream is = getClass().getResourceAsStream( "/config/4769-prevalance-stats.properties" );
		FileInputStream fileInputStream = new FileInputStream(new File(taskConfigPropertiesFilePath));
		
		properties.load(fileInputStream);
		
		Map<String,String> propMap = new HashMap<>();
		for(Object key: properties.keySet()){
			String keyStr = (String) key;
			propMap.put(keyStr, properties.getProperty(keyStr));
		}

		MapConfig config = new MapConfig(propMap);

		List<String> scorers = getConfigStringList(config, "fortscale.scorers");
		ScorerContext context = new ScorerContext(config);
		context.setBean("modelService", modelService);
		List<Scorer> scorersToRun = new ArrayList<>();
		for(String ScorerStr: scorers){
			Scorer scorer = (Scorer) context.resolve(Scorer.class, ScorerStr);
			checkNotNull(scorer);
			scorersToRun.add(scorer);
		}
	}
}
