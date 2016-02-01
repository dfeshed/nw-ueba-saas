package fortscale.streaming.scorer;

import fortscale.streaming.TaskTestUtil;
import fortscale.streaming.feature.extractor.FeatureExtractionService;
import fortscale.streaming.service.EventsScoreStreamTaskService;
import fortscale.streaming.service.config.StreamingTaskDataSourceConfigKey;
import junitparams.JUnitParamsRunner;
import org.apache.samza.config.Config;
import org.apache.samza.metrics.Counter;
import org.apache.samza.metrics.MetricsRegistry;
import org.apache.samza.task.TaskContext;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(JUnitParamsRunner.class)
public class EventsScoreStreamTaskServiceTest extends TaskScorerConfigTest{
	
	
	protected TaskContext context;
	protected MetricsRegistry metricsRegistry;
	protected Counter counter;
	
	@SuppressWarnings("resource")
	@BeforeClass
	public static void setUpClass(){
		new ClassPathXmlApplicationContext("classpath*:META-INF/spring/EventsScoreStreamTaskServiceTest-context.xml");
	}
	
	@Before
	public void setUp(){
		super.setUp();
		context = mock(TaskContext.class);
		metricsRegistry = mock(MetricsRegistry.class);
		counter = mock(Counter.class);
	}
	
	protected EventsScoreStreamTaskService createEventsScoreStreamTaskService(String taskConfigPropertiesFilePath, StreamingTaskDataSourceConfigKey configKey) throws Exception{
		when(context.getMetricsRegistry()).thenReturn(metricsRegistry);
		when(metricsRegistry.newCounter((String)anyObject(), (String)anyObject())).thenReturn(counter);
		
		Config dataSourceConfig = TaskTestUtil.buildPrevalenceTaskConfig(taskConfigPropertiesFilePath, configKey);
		EventsScoreStreamTaskService eventsScoreStreamTaskService = new EventsScoreStreamTaskService(dataSourceConfig, context, modelService, new FeatureExtractionService(dataSourceConfig));
		return eventsScoreStreamTaskService;
	}
	
}
