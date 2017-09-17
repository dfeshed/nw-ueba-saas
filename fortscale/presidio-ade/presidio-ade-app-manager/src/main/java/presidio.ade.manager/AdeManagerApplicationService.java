package presidio.ade.manager;

import presidio.ade.sdk.common.AdeManagerSdk;

import java.time.Duration;
import java.time.Instant;

/**
 * Created by maria_dorohin on 9/6/17.
 */
public class AdeManagerApplicationService {

    private AdeManagerSdk adeManagerSdk;
    private Duration enrichedTtl;
    private Duration enrichedCleanupInterval;

    public AdeManagerApplicationService(AdeManagerSdk adeManagerSdk, Duration enrichedTtl, Duration enrichedCleanupInterval){
        this.adeManagerSdk = adeManagerSdk;
        this.enrichedTtl = enrichedTtl;
        this.enrichedCleanupInterval = enrichedCleanupInterval;
    }

    /**
     * Cleanup enriched data until the given instance according to ttl duration and cleanup interval.
     * @param until
     */
    public void cleanupEnrichedData(Instant until){
        adeManagerSdk.cleanupEnrichedData(until, enrichedTtl, enrichedCleanupInterval);
    }
}
