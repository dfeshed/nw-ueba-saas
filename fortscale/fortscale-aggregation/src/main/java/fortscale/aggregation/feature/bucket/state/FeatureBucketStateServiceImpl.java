package fortscale.aggregation.feature.bucket.state;

import fortscale.aggregation.feature.bucket.repository.state.FeatureBucketStateRepository;
import fortscale.utils.logging.Logger;
import fortscale.utils.monitoring.stats.StatsService;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Created by alexp on 11/12/16.
 */
public class FeatureBucketStateServiceImpl implements FeatureBucketStateService {
    private static final Logger logger = Logger.getLogger(FeatureBucketStateService.class);

    private FeatureBucketStateRepository featureBucketStateRepository;

    private Instant lastClosedDailyBucketDate;

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
        logger.debug("updateFeatureBucketState with lastEventEpochtime {}", lastEventEpochtime);
        FeatureBucketState featureBucketState = getFeatureBucketState();
        Instant lastEventDate = Instant.ofEpochSecond(lastEventEpochtime);
        Instant lastEventDay = lastEventDate.truncatedTo(ChronoUnit.DAYS);
        boolean shouldSave = true;

        // Creating new state object
        if (lastClosedDailyBucketDate == null){
            featureBucketState = new FeatureBucketState(lastEventDay, lastEventDate);
        // Updating the last synced date in case that the last event date is after the lst sync date
        }else if (lastClosedDailyBucketDate.isBefore(lastEventDate)) {
            featureBucketState.setLastClosedDailyBucketDate(lastEventDay);
            lastClosedDailyBucketDate = lastEventDate;
            // Updating the lastSyncedEventDate
            featureBucketState.setLastSyncedEventDate(lastEventDate);
        // When the last event date is before the last synced date - shouldn't happen
        } else {
            logger.warn(
                    String.format("Trying to update last daily aggregation with with smaller date. The saved date is - %s, trying to save - %s",
                            lastClosedDailyBucketDate, lastEventDate));
            shouldSave = false;
        }

        try {
            if (shouldSave) {
                logger.debug("Before update FeatureBucketState with {}", featureBucketState);
                // Save the last synced event date to mongo
                FeatureBucketState savedState = featureBucketStateRepository.save(featureBucketState);

                logger.debug("Updated FeatureBucketState with {}", savedState);

                metrics.updateFeatureBucketStateSuccess++;
                metrics.lastClosedDailyBucketDate = savedState.getLastClosedDailyBucketDate().toEpochMilli();
                metrics.lastSyncedEventDate = savedState.getLastSyncedEventDate().toEpochMilli();
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
            lastClosedDailyBucketDate = featureBucketState.getLastClosedDailyBucketDate();
            logger.info("FeatureBucketStateService recovery finished, got {}", featureBucketState);
        }
    }

    @Override
    public FeatureBucketState getFeatureBucketState() {
        FeatureBucketState featureBucketState = featureBucketStateRepository.getState();

        if (featureBucketState != null){
            metrics.getFeatureBucketStateSuccess++;
        }else{
            metrics.updateFeatureBucketStateFailure++;
        }
        return featureBucketState;
    }
}
