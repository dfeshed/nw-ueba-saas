package fortscale.aggregation.feature.bucket.repository.state;

import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Created by alexp on 11/12/16.
 */
public class FeatureBucketStateServiceImpl implements FeatureBucketStateService {
    private static final Logger logger = Logger.getLogger(FeatureBucketStateService.class);

    @Autowired
    FeatureBucketStateRepository featureBucketStateRepository;

    private Instant date;

    protected FeatureBucketStateServiceImpl(FeatureBucketStateRepository featureBucketStateRepository) {
        this.featureBucketStateRepository = featureBucketStateRepository;
    }

    public FeatureBucketStateServiceImpl() {
    }

    @Override
    /**
     * Updating the last synced day
     */
    public void updateState(long lastEventEpochtime) {
        FeatureBucketState featureBucketState = null;
        boolean shouldSave = false;
        Instant newDate = Instant.ofEpochSecond(lastEventEpochtime).truncatedTo(ChronoUnit.DAYS);

        if (date == null){
            featureBucketState = getFeatureBucketState();
            if (featureBucketState != null){
                date = featureBucketState.getLastSyncedDate();
            }else{
                featureBucketState = new FeatureBucketState(newDate);
                shouldSave = true;
            }
        }

        if (!shouldSave) {
            if (date.isBefore(newDate)) {
                featureBucketState.setLastSyncedDate(newDate);
                date = newDate;
                shouldSave = true;
            } else {
                logger.warn(
                        String.format("Trying to update last daily aggregation with with smaller date. The saved date is - %s, trying to save - %s",
                                date, newDate));
            }
        }

        if (shouldSave){
            featureBucketStateRepository.save(featureBucketState);
        }
    }

    @Override
    public FeatureBucketState getFeatureBucketState() {
        return featureBucketStateRepository.getState();
    }
}
