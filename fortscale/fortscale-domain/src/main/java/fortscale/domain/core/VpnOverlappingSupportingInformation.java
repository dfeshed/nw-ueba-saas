package fortscale.domain.core;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import parquet.org.slf4j.Logger;
import parquet.org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.List;

/**
 * Created by Amir Keren on 03/09/15.
 */
@JsonTypeName("vpnOverlappingSupportingInformation")
public class VpnOverlappingSupportingInformation extends NotificationSupportingInformation {

    private static Logger logger = LoggerFactory.getLogger(NotificationSupportingInformation.class);

    private List<VpnSessionOverlap> rawEvents;

    public List<VpnSessionOverlap> getRawEvents() {
        return rawEvents;
    }

    public void setRawEvents(String json) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            rawEvents = mapper.readValue(json, new TypeReference<List<VpnSessionOverlap>>(){});
        } catch (IOException ex) {
            logger.error("String is not a valid JSON object {}", ex.getMessage());
        }
    }

    @Override
    public void setData(String json) {
        setRawEvents(json);
    }

}