package fortscale.aggregation.feature.bucket.repository.state;

import fortscale.utils.logging.Logger;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Created by alexp on 11/12/16.
 */
public class FeatureBucketStateServiceImpl implements FeatureBucketStateService {
    private static final Logger logger = Logger.getLogger(FeatureBucketStateService.class);

    FeatureBucketStateRepository featureBucketStateRepository;

    private Instant date;

    public FeatureBucketStateServiceImpl(FeatureBucketStateRepository featureBucketStateRepository) {
        this.featureBucketStateRepository = featureBucketStateRepository;
    }

    @Override
    public void updateState(long lastEventEpochtime, FeatureBucketState.StateType stateType) {
        FeatureBucketState featureBucketState = null;
        boolean shouldSave = false;
        Instant newDate = Instant.ofEpochSecond(lastEventEpochtime).truncatedTo(ChronoUnit.DAYS);

        if (date == null){
            featureBucketState = getFeatureBucketState(stateType);
            if (featureBucketState != null){
                date = featureBucketState.getAggregationFeatureStateDate().getDate();
            }else{
                featureBucketState = new FeatureBucketState(newDate, stateType);
                shouldSave = true;
            }
        }

        if (!shouldSave) {
            if (date.isBefore(newDate)) {
                featureBucketState.updateAggregationStateDate(newDate);
                date = newDate;
                shouldSave = true;
            } else {
                logger.warn("Trying to update last daily aggregation date with smaller date");
            }
        }

        if (shouldSave){
            featureBucketStateRepository.dosomeStuffupdateFeatureBucketState(featureBucketState);
        }
    }

    @Override
    public FeatureBucketState getFeatureBucketState(FeatureBucketState.StateType stateType) {
        return featureBucketStateRepository.findByStateType(stateType);
    }
}
