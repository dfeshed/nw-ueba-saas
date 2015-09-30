package fortscale.streaming;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.samza.Partition;
import org.apache.samza.config.MapConfig;
import org.apache.samza.metrics.MetricsRegistry;
import org.apache.samza.storage.kv.Entry;
import org.apache.samza.storage.kv.KeyValueIterator;
import org.apache.samza.storage.kv.KeyValueStore;
import org.apache.samza.task.TaskContext;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.streaming.service.aggregation.AggregatorManager;
import fortscale.utils.test.category.HadoopTestCategory;
import fortscale.utils.test.category.IntegrationTestCategory;




@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/spring/streaming-aggr-context.xml"})
@Category(HadoopTestCategory.class)
public class StreamingAggrTaskContextTestInt {

	@Test
	@Category(IntegrationTestCategory.class)
	public void testContext(){
		Map<String, String> configMap = new HashMap<String, String>();
		configMap.put(AggregatorManager.SAMZA_TASK_FORTSCALE_TIMESTAMP_FIELD_CONFIG_PATH, "timestamp");
		MapConfig mapConfig = new MapConfig(configMap);
		@SuppressWarnings("unused")
		AggregatorManager aggregatorManager = new AggregatorManager(mapConfig, new ExtendedSamzaTaskContext(new TaskContextImpl(), mapConfig));
	}
	
	private static class TaskContextImpl implements TaskContext{

		@Override
		public MetricsRegistry getMetricsRegistry() {
			return null;
		}

		@Override
		public Partition getPartition() {
			return null;
		}

		@Override
		public Object getStore(String name) {
			return new KeyValueStoreImpl();
		}
		
	}
	
	private static class KeyValueStoreImpl implements KeyValueStore<String, FeatureBucket>{

		@Override
		public FeatureBucket get(String key) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void put(String key, FeatureBucket value) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void putAll(List<Entry<String, FeatureBucket>> entries) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void delete(String key) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public KeyValueIterator<String, FeatureBucket> range(String from, String to) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public KeyValueIterator<String, FeatureBucket> all() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void close() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void flush() {
			// TODO Auto-generated method stub
			
		}
		
	}
}


