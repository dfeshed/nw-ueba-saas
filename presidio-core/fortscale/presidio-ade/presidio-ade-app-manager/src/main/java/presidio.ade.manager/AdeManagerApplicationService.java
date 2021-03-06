package presidio.ade.manager;

import fortscale.utils.logging.Logger;
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

    private static final Logger logger = Logger.getLogger(AdeManagerApplicationService.class);

    public AdeManagerApplicationService(AdeManagerSdk adeManagerSdk, Duration enrichedTtl, Duration enrichedCleanupInterval){
        this.adeManagerSdk = adeManagerSdk;
        this.enrichedTtl = enrichedTtl;
        this.enrichedCleanupInterval = enrichedCleanupInterval;
        enrichedTtlDurationValidation();
    }

    /**
     * Cleanup enriched data until the given instance according to ttl duration and cleanup interval.
     * @param until
     */
    public void cleanupEnrichedData(Instant until){
        adeManagerSdk.cleanupEnrichedData(until, enrichedTtl, enrichedCleanupInterval);
    }

    /**
     * Ensure that we don't clean last 24 hours
     */
    public void enrichedTtlDurationValidation(){
        if (enrichedTtl.compareTo(Duration.ofDays(1)) < 0){
            String s = String.format("Enriched ttl duration should be greater than 24 hours, enrichedTtl: %s.", enrichedTtl.toString());
            throw new IllegalArgumentException(s);
        }
    }
}
