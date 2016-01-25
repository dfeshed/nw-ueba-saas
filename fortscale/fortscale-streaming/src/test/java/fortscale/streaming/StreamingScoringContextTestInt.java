package fortscale.streaming;


import fortscale.ml.model.cache.ModelsCacheInfo;
import fortscale.ml.scorer.ScorersService;
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

import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/spring/streaming-scoring-context.xml"})
@Category(HadoopTestCategory.class)
public class StreamingScoringContextTestInt {

    @Autowired
    SamzaContainerService samzaContainerService;

    @Test
    @Category(IntegrationTestCategory.class)
    public void testContext() throws Exception {
        KeyValueStoreMock<String, ModelsCacheInfo> cache = new KeyValueStoreMock<>();
        String storeName = "the_store_name";
        Config config = mock(Config.class);
        when(config.get(ModelsCacheServiceSamza.STORE_NAME_PROPERTY)).thenReturn(storeName);
        TaskContext context = mock(TaskContext.class);
        when(context.getStore(storeName)).thenReturn(cache);
        samzaContainerService.init(config,context);
        ScoringTaskService scoringTaskService = new ScoringTaskService(config, context);
    }
}
