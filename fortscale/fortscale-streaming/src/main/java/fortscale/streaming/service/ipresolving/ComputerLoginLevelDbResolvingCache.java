package fortscale.streaming.service.ipresolving;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import fortscale.domain.events.ComputerLoginEvent;
import org.apache.samza.storage.kv.KeyValueStore;

/**
 * LevelDb based cache for Computer Login Events ip resolving updates
 */
public class ComputerLoginLevelDbResolvingCache extends LevelDbBasedResolvingCache<ComputerLoginEvent> {

    private ObjectMapper mapper;

    public ComputerLoginLevelDbResolvingCache(KeyValueStore<String, ComputerLoginEvent> store) {
        super(store);

        mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
    }

    @Override
    public void update(String ip, String event) throws Exception {
        // deserialize the event to ComputerLoginEvent
        ComputerLoginEvent loginEvent = mapper.readValue(event, ComputerLoginEvent.class);

        // add the event to the cache
        this.put(ip, loginEvent);
    }

}
