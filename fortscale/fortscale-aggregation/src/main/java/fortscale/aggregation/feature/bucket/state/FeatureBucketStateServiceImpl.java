package fortscale.aggregation.feature.bucket.state;

import fortscale.aggregation.feature.bucket.repository.state.FeatureBucketStateRepository;
import fortscale.utils.logging.Logger;
import fortscale.utils.monitoring.stats.StatsService;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Created by alexp on 11/12/16.
 */
public class FeatureBucketStateServiceImpl implements FeatureBucketStateService {
    private static final Logger logger = Logger.getLogger(FeatureBucketStateService.class);

    private FeatureBucketStateRepository featureBucketStateRepository;

    private StatsService statsService;

    private FeatureBucketStateServiceMetrics metrics;

    public FeatureBucketStateServiceImpl(FeatureBucketStateRepository featureBucketStateRepository, StatsService statsService) {
        this.featureBucketStateRepository = featureBucketStateRepository;
        this.statsService = statsService;
        metrics = new FeatureBucketStateServiceMetrics(statsService);
        recovery();
    }

    @Override
    /**
     * Updating the last synced day
     * lastEventEpochtime - the time of the last event synced to mongo. In seconds
     */
    public void updateFeatureBucketState(long lastEventEpochtime) {
        logger.debug("Entering updateFeatureBucketState method with lastEventEpochtime {}", lastEventEpochtime);

        FeatureBucketState featureBucketState = getFeatureBucketState();
        Instant lastEventDate = Instant.ofEpochSecond(lastEventEpochtime);
        boolean shouldSave = true;

        // Creating new state object
        if (featureBucketState == null){
            featureBucketState = new FeatureBucketState(lastEventDate);
        // Updating the last synced date in case that the last event date is after the lst sync date
        // but not more than 1 day from system time
        }else if (!lastEventDate.isAfter(Instant.now().plus(1, ChronoUnit.DAYS))){
            if (featureBucketState.getLastSyncedEventDate().isBefore(lastEventDate)) {
                // Updating the lastSyncedEventDate
                featureBucketState.setLastSyncedEventDate(lastEventDate);

            // When the last event date is before the last synced date - shouldn't happen
            } else {
                shouldSave = false;
                if(featureBucketState.getLastSyncedEventDate().isAfter(lastEventDate)) {
                    logger.warn(
                            String.format("Trying to update last daily aggregation with with smaller date. The saved date is - %s, trying to save - %s",
                                    getFeatureBucketState().getLastSyncedEventDate(), lastEventDate));
                }
            }
        }else {
            logger.warn("Trying to update last daily aggregation date with date that is too far ahead - {}", lastEventDate);
            shouldSave = false;
        }


        try {
            if (shouldSave) {
                logger.debug("Before updating FeatureBucketState mongo collection with {}", featureBucketState);
                // Save the last synced event date to mongo
                FeatureBucketState savedState = featureBucketStateRepository.save(featureBucketState);

                logger.info("Updated FeatureBucketState mongo collection with {}", savedState);

                metrics.updateFeatureBucketStateSuccess++;
                metrics.lastSyncedEventDate = savedState.getLastSyncedEventDate().getEpochSecond();
            }
        } catch (Exception e){
            logger.error("Error saving the feature bucket state to mongo. {}", featureBucketState, e);
            metrics.updateFeatureBucketStateFailure++;
        }
    }

    /**
     * Getting the lastClosedDailyBucketDate from mongo
     */
    private void recovery(){
        logger.info("Starting the recovery process of FeatureBucketStateService");
        FeatureBucketState featureBucketState = getFeatureBucketState();
        if (featureBucketState != null){
            // Getting the value of the last closed daily bucket
            logger.info("FeatureBucketStateService recovery finished, got {}", featureBucketState);
        }
    }

    @Override
    public FeatureBucketState getFeatureBucketState() {
        logger.debug("Getting feature bucket state");
        FeatureBucketState featureBucketState = featureBucketStateRepository.getState();

        if (featureBucketState != null){
            logger.debug("Got feature bucket state - {}", featureBucketState);
            metrics.getFeatureBucketStateSuccess++;
        }else{
            logger.debug("Got feature bucket state null");
            metrics.getFeatureBucketStateReturnedNull++;
        }
        return featureBucketState;
    }

    @Override
    public Instant getLastClosedDailyBucketDate() {
        FeatureBucketState featureBucketState = getFeatureBucketState();
        if (featureBucketState != null && featureBucketState.getLastSyncedEventDate() != null){
            return featureBucketState.getLastSyncedEventDate().truncatedTo(ChronoUnit.DAYS).minus(Duration.ofDays(1));
        }
        return null;
    }
}
