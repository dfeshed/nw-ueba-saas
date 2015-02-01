package fortscale.services.cache;

import com.google.common.cache.Cache;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class CacheHandlerTest {

	private CacheHandler<String,String> subject;

	@Before
	public void setUp() {
		subject = mock(CacheHandler.class);
	}

	@Test
	public void get_should_return_inner_cache_data() throws Exception {
		doCallRealMethod().when(subject).putFromString("key", "value");
		subject.putFromString("key","value");
		verify(subject).put(eq("key"), any(String.class));
	}

	@Test
	public void containsKey_should_return_true_if_the_key_is_in_cache() throws Exception {
		when(subject.get(anyString())).thenReturn("value");
		doCallRealMethod().when(subject).containsKey(anyString());
		boolean containKey =  subject.containsKey("key");
		assertEquals(true, containKey);
	}

	@Test
	public void containsKey_should_return_false_if_the_key_is_not_in_cache() throws Exception {
		when(subject.get(anyString())).thenReturn(null);
		doCallRealMethod().when(subject).containsKey(anyString());
		boolean containKey = subject.containsKey("unknownKey");
		assertEquals(false, containKey);
	}

}