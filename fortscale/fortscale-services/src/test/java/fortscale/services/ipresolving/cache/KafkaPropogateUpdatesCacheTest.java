package fortscale.services.ipresolving.cache;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import fortscale.services.cache.CacheHandler;
import fortscale.services.cache.KafkaPropogateUpdatesCache;
import fortscale.utils.kafka.KafkaEventsWriter;
import org.junit.*;

public class KafkaPropogateUpdatesCacheTest {

    private KafkaPropogateUpdatesCache<String,String> subject;
    private CacheHandler<String,String> innerCache;
    private KafkaEventsWriter kafkaWriter;

    @Before
    public void setUp() {
        innerCache = mock(CacheHandler.class);
        kafkaWriter = mock(KafkaEventsWriter.class);

        subject = new KafkaPropogateUpdatesCache<String,String>(innerCache, kafkaWriter,String.class);
    }

    @Test
    public void get_should_return_inner_cache_data() {
        when(innerCache.get("1.1.1.1")).thenReturn("x");

        String actual = subject.get("1.1.1.1");
        assertEquals("x", actual);
    }

    @Test
    public void put_should_publish_the_change_on_kafka() {
        subject.put("1.1.1.1", "x");
        verify(kafkaWriter, times(1)).send("1.1.1.1", "\"x\"");
    }


}