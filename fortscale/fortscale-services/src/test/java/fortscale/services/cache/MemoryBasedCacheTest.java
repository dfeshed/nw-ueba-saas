package fortscale.services.cache;

import com.google.common.cache.Cache;
import fortscale.utils.kafka.KafkaEventsWriter;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;

public class MemoryBasedCacheTest {

	private MemoryBasedCache<String,String> subject;

	private Cache<String,String> cache;

	@Before
	public void setUp() {
		subject = new MemoryBasedCache<String,String>(0,0,String.class);
		cache = spy(subject.getCache());
		subject.setCache(cache);
	}
	@Test
	public void get_should_return_inner_cache_data() throws IOException {
			when(cache.getIfPresent("1.1.1.1")).thenReturn("x");

			String actual = subject.get("1.1.1.1");
			assertEquals("x", actual);
	}

	@Test
	public void remove_should_remove_value_from_inner_cache() {
		subject.remove("1.1.1.1");
		verify(cache, times(1)).invalidate("1.1.1.1");
	}

	@Test
	public void put_should_put_the_value_in_the_inner_cache() {
		subject.put("1.1.1.1", "x");
		verify(cache, times(1)).put("1.1.1.1", "x");
	}

	@Test
	public void close_should_cleanup_the_inner_cache() throws IOException {
		subject.close();
		verify(cache, times(1)).cleanUp();
	}

}