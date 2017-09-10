package presidio.ade.manager;

import presidio.ade.sdk.common.AdeManagerSdk;

import java.time.Duration;
import java.time.Instant;

/**
 * Created by maria_dorohin on 9/6/17.
 */
public class ManagerApplicationService {

    private AdeManagerSdk adeManagerSdk;
    private Duration ttl;
    private Duration cleanup;

    public ManagerApplicationService(AdeManagerSdk adeManagerSdk, Duration ttl, Duration cleanup){
        this.adeManagerSdk = adeManagerSdk;
        this.ttl = ttl;
        this.cleanup = cleanup;
    }

    public void cleanupEnrichedCollections(Instant instant){
        adeManagerSdk.cleanupEnrichedCollections(instant, ttl, cleanup);
    }
}
