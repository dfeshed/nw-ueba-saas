package fortscale.streaming.task;

import fortscale.domain.core.EntitySupportingInformation;
import fortscale.domain.core.NotificationSupportingInformation;
import org.json.JSONException;
import org.json.JSONObject;
import parquet.org.slf4j.Logger;
import parquet.org.slf4j.LoggerFactory;

/**
 * Created by Amir Keren on 02/09/15.
 */
public class NotificationSupportingInformationPopulator implements EntitySupportingInformationPopulator {

    private static Logger logger = LoggerFactory.getLogger(NotificationSupportingInformationPopulator.class);

    @Override
    public EntitySupportingInformation populate(String data) {
        NotificationSupportingInformation notificationSupportingInformation = new NotificationSupportingInformation();
        try {
            notificationSupportingInformation.setData(new JSONObject(data));
        } catch (JSONException ex) {
            logger.error("String is not a valid JSON object {}", ex.getMessage());
        }
        return notificationSupportingInformation;
    }

}