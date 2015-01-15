package fortscale.streaming.service.ipresolving;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import fortscale.domain.events.DhcpEvent;
import org.apache.samza.storage.kv.KeyValueStore;

/**
 * LevelDb based cache for DHCP ip resolving updates
 */
public class DhcpLevelDbResolvingCache extends LevelDbBasedResolvingCache<DhcpEvent> {

    private ObjectMapper mapper;

    public DhcpLevelDbResolvingCache(KeyValueStore<String, DhcpEvent> store) {
        super(store);

        mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
    }

    @Override
    public void update(String ip, String event) throws Exception {
        // deserialize the event to DhcpEvent
        DhcpEvent dhcpEvent = mapper.readValue(event, DhcpEvent.class);

        // add the event to the cache
        this.put(ip, dhcpEvent);
    }
}
