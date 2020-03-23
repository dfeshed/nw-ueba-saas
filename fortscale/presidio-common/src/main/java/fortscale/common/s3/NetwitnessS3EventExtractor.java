package fortscale.common.s3;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.utils.s3.IS3MapExtractor;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class NetwitnessS3EventExtractor implements IS3MapExtractor {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public Map<String, Object> extract(String eventStr) throws IOException {
        NetwitnessS3Event event = MAPPER.readValue(eventStr, NetwitnessS3Event.class);
        return event.records.get(0);
    }


    private static final class NetwitnessS3Event {
        private List<Map<String, Object>> records;

        public List<Map<String, Object>> getRecords() {
            return records;
        }

        public void setRecords(List<Map<String, Object>> records) {
            this.records = records;
        }
    }
}
