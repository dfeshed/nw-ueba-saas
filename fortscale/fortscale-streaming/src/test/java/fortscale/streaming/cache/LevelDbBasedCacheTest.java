package fortscale.streaming.cache;

import com.google.common.cache.Cache;
import fortscale.services.cache.MemoryBasedCache;
import org.apache.samza.storage.kv.Entry;
import org.apache.samza.storage.kv.KeyValueIterator;
import org.apache.samza.storage.kv.KeyValueStore;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class LevelDbBasedCacheTest {

	private LevelDbBasedCache<String,String> subject;

	private KeyValueStore<String,String> store;

	@Before
	public void setUp() {
		store = mock(KeyValueStore.class);
		subject = new LevelDbBasedCache<String,String>(store,String.class);
	}

	@Test
	public void get_should_return_inner_cache_data() throws IOException {
			when(store.get("1.1.1.1")).thenReturn("x");

			String actual = subject.get("1.1.1.1");
			assertEquals("x", actual);
	}

	@Test
	public void remove_should_remove_value_from_inner_cache() {
		subject.remove("1.1.1.1");
		verify(store, times(1)).delete("1.1.1.1");
	}

	@Test
	public void put_should_put_the_value_in_the_inner_cache() {
		subject.put("1.1.1.1", "x");
		verify(store, times(1)).put("1.1.1.1", "x");
	}

	@Test
	public void close_should_flush_the_inner_cache() throws IOException {
		subject.close();
		verify(store, times(1)).flush();
	}

	@Test
	public void clear_should_clear_the_inner_cache_from_all_its_records_using_2_external_iterations() throws IOException {
		KeyValueIterator<String,String> keyValueIteratorFirstIteration = mock(KeyValueIterator.class);
		when(keyValueIteratorFirstIteration.hasNext()).thenReturn(true);
		when(keyValueIteratorFirstIteration.next()).thenReturn(new Entry<String, String>("key","value"));
		KeyValueIterator<String,String> keyValueIteratorSecondIteration = mock(KeyValueIterator.class);
		when(keyValueIteratorSecondIteration.hasNext()).thenReturn(true).thenReturn(false);
		when(keyValueIteratorSecondIteration.next()).thenReturn(new Entry<String, String>("key","value"));
		when(store.all()).thenReturn(keyValueIteratorFirstIteration).thenReturn(keyValueIteratorSecondIteration);
		subject.clear();
		verify(store, times(3)).delete(anyString());
	}

}