package fortscale.services.cache;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.google.common.cache.Cache;

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
	public void clear_should_invalidateAll_entries_in_the_inner_cache() throws IOException {
		subject.clear();
		verify(cache, times(1)).invalidateAll();
	}

	@Test
	public void close_should_cleanup_the_inner_cache() throws IOException {
		subject.close();
		verify(cache, times(1)).cleanUp();
	}

}