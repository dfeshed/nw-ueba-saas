package fortscale.utils.s3;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class MapExtractor implements IMapExtractor{

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public Map<String, Object> extract(String eventStr) throws IOException {
        NetwitnessEvent event = MAPPER.readValue(eventStr, NetwitnessEvent.class);
        return event.records.get(0);
    }


    public static class NetwitnessEvent{
        private List<Map<String, Object>> records;

        public List<Map<String, Object>> getRecords() {
            return records;
        }

        public void setRecords(List<Map<String, Object>> records) {
            this.records = records;
        }
    }
}
