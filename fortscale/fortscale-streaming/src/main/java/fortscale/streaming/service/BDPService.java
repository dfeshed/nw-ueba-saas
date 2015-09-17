package fortscale.streaming.service;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;

/**
 * Created by Amir Keren on 17/09/15.
 */
@Configurable(preConstruction = true)
public class BDPService {

    @Value("${fortscale.bdp.run}")
    private boolean isBDPRunning;
    @Value("${collection.evidence.notification.topic}")
    private String notificationTopic;

    public boolean isBDPRunning() {
        return isBDPRunning;
    }

}