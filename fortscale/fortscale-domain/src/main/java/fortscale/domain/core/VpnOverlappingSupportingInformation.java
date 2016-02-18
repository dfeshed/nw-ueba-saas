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

    private static Logger logger = LoggerFactory.getLogger(VpnOverlappingSupportingInformation.class);

    private List<VpnSessionOverlap> rawEvents;

    public List<VpnSessionOverlap> getRawEvents() {
        return rawEvents;
    }

    public VpnOverlappingSupportingInformation(){}

    @Override
    public void setData(Evidence evidence, String json, boolean isBDPRunning) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            rawEvents = mapper.readValue(json, new TypeReference<List<VpnSessionOverlap>>(){});
        } catch (IOException ex) {
            logger.error("String is not a valid JSON object {}", ex.getMessage());
        }
    }


    public void setRawEvents(List<VpnSessionOverlap> rawEvents) {
        this.rawEvents = rawEvents;
    }

}