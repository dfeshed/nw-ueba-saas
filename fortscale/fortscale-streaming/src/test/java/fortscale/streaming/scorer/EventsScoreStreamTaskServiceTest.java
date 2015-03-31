package fortscale.streaming.scorer;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.samza.config.Config;
import org.apache.samza.metrics.Counter;
import org.apache.samza.metrics.MetricsRegistry;
import org.apache.samza.task.TaskContext;
import org.junit.Before;

import fortscale.streaming.TaskTestUtil;
import fortscale.streaming.feature.extractor.FeatureExtractionService;
import fortscale.streaming.service.EventsScoreStreamTaskService;

public class EventsScoreStreamTaskServiceTest extends TaskScorerConfigTest{
	
	private TaskContext context;
	private MetricsRegistry metricsRegistry;
	private Counter counter;
	
	@Before
	public void setUp(){
		super.setUp();
		context = mock(TaskContext.class);
		metricsRegistry = mock(MetricsRegistry.class);
		counter = mock(Counter.class);
	}
	
	protected EventsScoreStreamTaskService createEventsScoreStreamTaskService(String taskConfigPropertiesFilePath) throws Exception{
		Config config = TaskTestUtil.buildTaskConfig(taskConfigPropertiesFilePath);
		when(context.getMetricsRegistry()).thenReturn(metricsRegistry);
		when(metricsRegistry.newCounter((String)anyObject(), (String)anyObject())).thenReturn(counter);
		
		EventsScoreStreamTaskService eventsScoreStreamTaskService = new EventsScoreStreamTaskService(config, context, modelService, new FeatureExtractionService(config));
		return eventsScoreStreamTaskService;
	}
	
}
