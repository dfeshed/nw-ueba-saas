package fortscale.aggregation.feature.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import fortscale.aggregation.DataSourcesSyncTimerListener;
import fortscale.aggregation.feature.bucket.strategy.FeatureBucketStrategyData;
import fortscale.aggregation.feature.bucket.strategy.NextBucketEndTimeListener;
import fortscale.utils.logging.Logger;

/**
 * AggrFeatureEventData maintains a list of bucketTicks and other information that is used by the associated
 * AggrFeatureEventBuilder to generate Aggregated Feature Events of a specific type for a specific context.
 */
class AggrFeatureEventData implements NextBucketEndTimeListener {
    private static final Logger logger = Logger.getLogger(AggrFeatureEventData.class);
    private AggrFeatureEventBuilder builder;
    private List<BucketTick> bucketTicks;
    private Map<String, String> context;
    private String firstBucketStrategyId;
    private BucketTick lastSentEventBucketTick;

    AggrFeatureEventData(AggrFeatureEventBuilder builder, Map<String, String> context, String firstBucketStrategyId) {
        Assert.notNull(builder);
        Assert.notNull(context);
        Assert.notNull(firstBucketStrategyId);

        this.builder = builder;
        this.context = context;
        this.bucketTicks = new ArrayList<>();
        this.firstBucketStrategyId = firstBucketStrategyId;
    }

    Map<String, String> getContext() {
        return context;
    }

    void setLastSentEventBucketTick(BucketTick lastSentEventBucketTick) {
        this.lastSentEventBucketTick = lastSentEventBucketTick;
    }

    boolean doesItMatchBucketLeap(BucketTick bucketTick) {
        int bucketDataIndex = bucketTicks.indexOf(bucketTick);
        int lastSentBucketDataIndex = lastSentEventBucketTick ==null? -1 : bucketTicks.indexOf(lastSentEventBucketTick);
        return Math.abs(bucketDataIndex - lastSentBucketDataIndex) % builder.getConf().getBucketsLeap() == 0;
    }

    AggrFeatureEventBuilder getBuilder() {
        return builder;
    }

    String getFirstBucketStrategyId() {
        return firstBucketStrategyId;
    }


    void dataSourcesReachedTime(BucketTick bucketTick) {
        builder.dataSourcesReachedTime(this, bucketTick);
    }

    /**
     * Adding the bucketId to the bucketTicks list.
     * The bucket start time MUST be later the the end time of the last bucket in the list OR
     * if the last item contains only strategy data (i.e. without bucket ID) then it must match its times.
     * No check is done whether the bucketId matches this EventData context.
     */
    BucketTick addBucketID(String bucketID, Long startTime, Long endTime) {
        // Assertions
        Assert.isTrue(endTime > startTime && startTime > 0);
        Assert.notNull(bucketID);
        Assert.isTrue(StringUtils.isNotEmpty(bucketID));

        BucketTick bucketTick = null;

        //check if the bucketId is related to any strategyData already in the list
        boolean found = false;
        if(bucketTicks.size()>0) {
            bucketTick = bucketTicks.get(bucketTicks.size()-1);

            // If the last item in the list contains empty bucket tick (i.e. no bucket ID
            // and only strategy ID) then the new bucket might related to it or it might
            // be a later bucket, meaning that no bucket was created at that bucket tick.
            // (This can happen only with FIX-TIME strategy.)
            if(bucketTick.strategyData!=null && bucketTick.getBucketId()==null
                    && bucketTick.strategyData.getStartTime() == startTime
                    && bucketTick.strategyData.getEndTime() == endTime) {

                bucketTick.bucketId = bucketID;
                bucketTick.startTime = startTime;
                bucketTick.endTime = endTime;
                found = true;
            } else {
                // The last item in the list contains bucketId so the new bucket should be later in time from it
                if(startTime <= bucketTick.getEndTime()) {
                    String errorMessage = String.format("Assumption of bucket creation order failed! new bucket values: bucketId: %s startTime: %d endTime: %d last bucket values:  bucketId: %s startTime: %d endTime: %d",
                            bucketID, startTime, endTime, bucketTick.getBucketId(), bucketTick.getStartTime(), bucketTick.getEndTime());
                    logger.error(errorMessage);
                }
            }
        }
        if(!found) {
            bucketTick = createAndInsertBucketTick(bucketID, startTime, endTime, null);
        }
        return bucketTick;
    }

    List<BucketTick> getBucketTicks() {
        ArrayList<BucketTick> clone = new ArrayList<>(bucketTicks.size());
        for(BucketTick bucketTick : bucketTicks) {
            clone.add(bucketTick);
        }
        return clone;
    }

    private BucketTick createAndInsertBucketTick(String bucketID, long startTime, long endTime, FeatureBucketStrategyData strategyData) throws RuntimeException{

        BucketTick bucketTick = new BucketTick(bucketID, startTime, endTime, strategyData, this);

        if(bucketTicks.size()==0) {
            bucketTicks.add(bucketTick);
        } else {
            boolean added = false;
            for (int i = bucketTicks.size() - 1; i >= 0; i--) {
                BucketTick bd = bucketTicks.get(i);

                if( ( bd.getStartTime()==startTime && bd.getEndTime()!=endTime )
                        || ( bd.getEndTime()!=startTime && bd.getStartTime()==endTime ) ) {

                    String errorMessage = String.format("New bucket start or end time matches existing bucketTick time, but the other start/end do not match. New bucket values: bucketId: %s, startTime: %d, endTime: %d. Existing bucket data values:  bucketId: %s, startTime: %d, endTime: %d",
                            bucketID, startTime, endTime, bd.getBucketId(), bd.getStartTime(), bd.getEndTime());
                    throw new RuntimeException(errorMessage);

                }

                if( bd.getStartTime()==startTime ) { // && bd.getEndTime()==endTime is always true when reaching here so no need to perform this operation
                    if(bd.getBucketId()!=null) {
                        String errorMessage = String.format("Bucket ID already exists in bucketTick. New bucketTick values: bucketId: %s, startTime: %d, endTime: %d.",
                                bucketID, startTime, endTime);
                        throw new RuntimeException(errorMessage);

                    } else {
                        bd.setBucketId(bucketID);
                        added = true;
                        bucketTick = bd;
                        break;
                    }
                } else if (bd.getEndTime() < startTime) {
                    addBucketDataAndFillInMissingBucketTicks(i + 1, bucketTick);
                    added = true;
                    break;
                }
            }
            if(!added) {
                addBucketDataAndFillInMissingBucketTicks(0, bucketTick);
            }
        }

        return bucketTick;
    }

    private void addBucketDataAndFillInMissingBucketTicks(int position, BucketTick bucketTick) {

        bucketTicks.add(position, bucketTick);

        if(position>0) {
            // Check for missing bucket ticks
            BucketTick previousBucketTick = bucketTicks.get(position-1);
            boolean notAllMissingBucketTicksAdded = true;
            while (notAllMissingBucketTicksAdded) {
                FeatureBucketStrategyData featureBucketStrategyData = builder.getBucketStrategy().getNextBucketStrategyData(builder.getConf().getBucketConf(), getFirstBucketStrategyId(), previousBucketTick.getEndTime());

                if (featureBucketStrategyData == null) {
                    String errorMessage = String.format("Strategy.getNextBucketStrategyData(conf name: %s, strategy id: %s, previous bucket end time: %d) returned null but future bucket exists: bucketTick.startTime=%d, bucketTick.endTime=%d",
                            builder.getConf().getBucketConf().getName(), getFirstBucketStrategyId(), previousBucketTick.getEndTime(), bucketTick.getStartTime(), bucketTick.getEndTime());
                    throw new RuntimeException(errorMessage);
                }

                if (featureBucketStrategyData.getStartTime() < bucketTick.getStartTime()) {
                    previousBucketTick = new BucketTick(null, featureBucketStrategyData.getStartTime(), featureBucketStrategyData.getEndTime(), featureBucketStrategyData, this);
                    bucketTicks.add(position, previousBucketTick);
                    position++;
                } else if(featureBucketStrategyData.getStartTime() == bucketTick.getStartTime()) {
                    notAllMissingBucketTicksAdded = false;
                } else {
                    String errorMessage = String.format("Strategy.getNextBucketStrategyData(conf name: %s, strategy id: %s, previous bucket end time: %d) returned strategy data with start time %d which is bigger then the currently added bucketTick start time %d",
                            builder.getConf().getBucketConf().getName(), getFirstBucketStrategyId(), previousBucketTick.getEndTime(), featureBucketStrategyData.getStartTime(), bucketTick.getStartTime());
                    throw new RuntimeException(errorMessage);
                }
            }
        }
    }

    /**
     * Adds the strategyData to the bucketTicks list.
     */
    @Override
    public void nextBucketEndTimeUpdate(FeatureBucketStrategyData strategyData) {
        // Assertions
        Assert.notNull(strategyData);
        BucketTick bucketTick = createAndInsertBucketTick(null, strategyData.getStartTime(), strategyData.getEndTime(), strategyData);
        builder.registerInTimerForNextBucketEndTime(bucketTick, strategyData.getEndTime());
    }


    /**
     * Updates the end time of the given bucketId.
     * The bucketId MUST be the last bucketId in the bucketTicks list(otherwise it is an error in the program flow).
     */
    BucketTick setEndTime(String bucketID, Long endTime) {
        Assert.isTrue(bucketTicks.size() > 0);
        BucketTick bucketTick = bucketTicks.get(bucketTicks.size()-1);
        Assert.isTrue(bucketTick.getBucketId() != null && StringUtils.equals(bucketTick.getBucketId(), bucketID));
        bucketTick.setEndTime(endTime);
        return bucketTick;
    }

    void clearOldBucketData() {
        // Cleaning old buckets
        int minBucketToLeave = Math.max(builder.getConf().getBucketsLeap(), builder.getConf().getNumberOfBuckets())+1;
        int largetBucketLeapIndexFoundThatWasSent = -1;
        for (int i = 0; i < bucketTicks.size(); i++) {
            BucketTick bucketTick = bucketTicks.get(i);
            if(doesItMatchBucketLeap(bucketTick)) {
                if(bucketTick.isProcessedAsLeadingBucket()) {
                    largetBucketLeapIndexFoundThatWasSent=i;
                } else {
                    break;
                }
            }
        }
        int howManyToRemove=largetBucketLeapIndexFoundThatWasSent+1;
        howManyToRemove = bucketTicks.size() - howManyToRemove > minBucketToLeave ? howManyToRemove : bucketTicks.size() - minBucketToLeave;

        for ( int i=0; i < howManyToRemove; i++) {
            BucketTick bucketTick = bucketTicks.remove(0);
            if(bucketTick.getBucketId()!=null) {
                builder.removeBucketId2eventDataMapping(bucketTick.getBucketId());
            }
        }
    }

    class BucketTick implements DataSourcesSyncTimerListener {
        private FeatureBucketStrategyData strategyData;
        private String bucketId;
        private Long startTime;
        private Long endTime;
        private AggrFeatureEventData aggrFeatureEventData;
        private long syncTimerRegistrationID;
        private boolean processedAsLeadingBucket;


        public BucketTick(String bucketId, Long startTime, Long endTime, FeatureBucketStrategyData strategyData, AggrFeatureEventData aggrFeatureEventData) {
            this.bucketId = bucketId;
            this.startTime = startTime;
            this.endTime = endTime;
            this.strategyData = strategyData;
            this.aggrFeatureEventData = aggrFeatureEventData;
        }

        public boolean isProcessedAsLeadingBucket() {
            return processedAsLeadingBucket;
        }

        public void setProcessedAsLeadingBucket(boolean processedAsLeadingBucket) {
            this.processedAsLeadingBucket = processedAsLeadingBucket;
        }

        public FeatureBucketStrategyData getStrategyData() {
            return strategyData;
        }

        public String getBucketId() {
            return bucketId;
        }

        public long getEndTime() {
            return endTime!=null ? endTime : strategyData.getEndTime();
        }

        public long getStartTime() {
            return startTime;
        }

        public void setEndTime(Long endTime) {
            this.endTime = endTime;
        }

        @Override
        public void dataSourcesReachedTime() {
            aggrFeatureEventData.dataSourcesReachedTime(this);
        }

        public void setSyncTimerRegistrationID(long syncTimerRegistrationID) {
            this.syncTimerRegistrationID = syncTimerRegistrationID;
        }

        public long getSyncTimerRegistrationID() {
            return syncTimerRegistrationID;
        }

        public Object getEventData() {
            return aggrFeatureEventData;
        }

        public void setBucketId(String bucketId) {
            this.bucketId = bucketId;
        }
    }

}