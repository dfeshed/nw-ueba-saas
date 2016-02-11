package fortscale.streaming;

import fortscale.ml.model.cache.ModelsCacheInfo;
import fortscale.streaming.common.SamzaContainerService;
import fortscale.streaming.service.model.ModelsCacheServiceSamza;
import fortscale.streaming.service.scorer.ScoringTaskService;
import fortscale.streaming.task.KeyValueStoreMock;
import fortscale.utils.test.category.HadoopTestCategory;
import fortscale.utils.test.category.IntegrationTestCategory;
import org.apache.samza.config.Config;
import org.apache.samza.task.TaskContext;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/spring/streaming-raw-events-scoring-context.xml"})
@Category(HadoopTestCategory.class)
public class StreamingRawEventsScoringContextTestInt {
    @Autowired
    private SamzaContainerService samzaContainerService;

    @Test
    @Category(IntegrationTestCategory.class)
    public void testContext() throws Exception {
        KeyValueStoreMock<String, ModelsCacheInfo> cache = new KeyValueStoreMock<>();
        Config config = TaskTestUtil.buildTaskConfig("config/raw-events-scoring-task.properties");
        TaskContext context = mock(TaskContext.class);
        String storeName = config.get(ModelsCacheServiceSamza.STORE_NAME_PROPERTY);
        when(context.getStore(storeName)).thenReturn(cache);
        samzaContainerService.init(config, context);
        new ScoringTaskService(config, context);
    }
}
