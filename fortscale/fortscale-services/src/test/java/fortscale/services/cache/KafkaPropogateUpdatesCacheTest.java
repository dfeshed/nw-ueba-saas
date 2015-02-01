package fortscale.services.cache;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import fortscale.utils.kafka.KafkaEventsWriter;
import org.junit.*;

import java.io.IOException;

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
    public void remove_should_remove_value_from_inner_cache() {
        subject.remove("1.1.1.1");
        verify(innerCache, times(1)).remove("1.1.1.1");
    }

    @Test
    public void put_should_put_the_value_in_the_inner_cache() {
        subject.put("1.1.1.1", "x");
        verify(innerCache, times(1)).put("1.1.1.1", "x");
    }

    @Test
    public void put_should_put_the_change_on_kafka() {
        subject.put("1.1.1.1", "x");
        verify(kafkaWriter, times(1)).send("1.1.1.1", "\"x\"");
    }

    @Test
    public void close_should_close_the_inner_cache() throws IOException {
        subject.close();
        verify(innerCache, times(1)).close();
    }

    @Test
    public void clear_should_clear_the_inner_cache() throws IOException {
        subject.clear();
        verify(innerCache, times(1)).clear();
    }

    @Test
    public void close_should_close_the_kafka_writer() throws IOException {
        subject.close();
        verify(kafkaWriter, times(1)).close();
    }
}