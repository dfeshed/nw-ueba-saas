package fortscale.domain.core;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.domain.events.VpnSession;
import parquet.org.slf4j.Logger;
import parquet.org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.List;

/**
 * supporting information for notification evidences - map of keys and values changing based on the type of notification
 * Created by Amir Keren on 02/09/2015.
 */

@JsonTypeName("notificationSupportingInformation")
public class NotificationSupportingInformation extends EntitySupportingInformation {

    private static Logger logger = LoggerFactory.getLogger(NotificationSupportingInformation.class);

    private List<VpnSessionOverlap> data;

    public void setData(String json) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            data = mapper.readValue(json, new TypeReference<List<VpnSessionOverlap>>(){});
        } catch (IOException ex) {
            logger.error("String is not a valid JSON object {}", ex.getMessage());
        }
    }

    public List<VpnSessionOverlap> getData() {
        return data;
    }


}