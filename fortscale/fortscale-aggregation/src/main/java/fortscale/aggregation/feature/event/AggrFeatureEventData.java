package fortscale.aggregation.feature.event;

import fortscale.aggregation.DataSourcesSyncTimerListener;
import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.strategy.FeatureBucketStrategyData;
import fortscale.aggregation.feature.bucket.strategy.NextBucketEndTimeListener;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * AggrFeatureEventData maintains a list of bucketIDs and other information that is used by the associated
 * AggrFeatureEventBuilder to generate Aggregated Feature Events of a specific type for a specific context.
 */
class AggrFeatureEventData implements NextBucketEndTimeListener {
    private AggrFeatureEventBuilder builder;
    private List<BucketData> bucketIDs;
    private Map<String, String> context;
    private String firstBucketStrategyId;
    private AggrFeatureEventData.BucketData lastSentEventBucketData;

    /**
     *
     * @param builder
     * @param context
     */
    AggrFeatureEventData(AggrFeatureEventBuilder builder, Map<String, String> context, String firstBucketStrategyId) {
        Assert.notNull(builder);
        Assert.notNull(context);
        Assert.notNull(firstBucketStrategyId);

        this.builder = builder;
        this.context = context;
        this.bucketIDs = new ArrayList<>();
        this.firstBucketStrategyId = firstBucketStrategyId;
    }

    Map<String, String> getContext() {
        return context;
    }

    void setLastSentEventBucketData(BucketData lastSentEventBucketData) {
        this.lastSentEventBucketData = lastSentEventBucketData;
    }

    boolean doesItMatchBucketLeap(BucketData bucketData) {
        int bucketDataIndex = bucketIDs.indexOf(bucketData);
        int lastSentBucketDataIndex = lastSentEventBucketData==null? -1 : bucketIDs.indexOf(lastSentEventBucketData);
        return Math.abs(bucketDataIndex - lastSentBucketDataIndex) % builder.getConf().getBucketsLeap() == 0;
    }

    AggrFeatureEventBuilder getBuilder() {
        return builder;
    }

    String getFirstBucketStrategyId() {
        return firstBucketStrategyId;
    }


    void dataSourcesReachedTime(BucketData bucketData) {
        builder.dataSourcesReachedTime(this, bucketData);
    }

    /**
     * Adding the bucketID to the bucketIDs list.
     * The bucket start time MUST be later the the end time of the last bucket in the list OR
     * if the last item contains only strategy data (i.e. without bucket ID) then it must match its times.
     * No check is done whether the bucketID matches this EventData context.
     * @param bucketID
     * @param startTime
     * @param endTime
     */
    BucketData addBucketID(String bucketID, Long startTime, Long endTime) {
        // Assertions
        Assert.isTrue(endTime > startTime && startTime > 0);
        Assert.notNull(bucketID);
        Assert.isTrue(StringUtils.isNotEmpty(bucketID));

        BucketData bucketData = null;

        //check if the bucketID is related to any strategyData already in the list
        boolean found = false;
        if(bucketIDs.size()>0) {
            bucketData = bucketIDs.get(bucketIDs.size()-1);

            // If the last item in the list contains empty bucket tick (i.e. no bucket ID
            // and only strategy ID) then the new bucket might related to it or it might
            // be a later bucket, meaning that no bucket was created at that bucket tick.
            // (This can happen only with FIX-TIME strategy.)
            if(bucketData.strategyData!=null && bucketData.getBucketID()==null
                    && bucketData.strategyData.getStartTime() == startTime
                    && bucketData.strategyData.getEndTime() == endTime) {

                bucketData.bucketID = bucketID;
                bucketData.startTime = startTime;
                bucketData.endTime = endTime;
                found = true;
            } else {
                // The last item in the list contains bucketID
                Assert.isTrue(startTime > bucketData.getEndTime());
            }
        }
        if(!found) {
            bucketData = new BucketData(bucketID, startTime, endTime, this);
            addBucketData(bucketData);
        }
        return bucketData;
    }

    List<BucketData> getBucketIDs() {
        ArrayList<BucketData> clone = new ArrayList<>(bucketIDs.size());
        for(BucketData bucketData : bucketIDs) {
            clone.add(bucketData);
        }
        return clone;
    }

    private void addBucketData(BucketData bucketData) {
        Assert.notNull(bucketData);
        bucketIDs.add(bucketData);
    }

    /**
     * Adds the strategyData to the bucketIDs list.
     * The bucket start time MUST be later the the end time of the last bucket in the list.
     * @param strategyData
     */
    @Override
    public void nextBucketEndTimeUpdate(FeatureBucketStrategyData strategyData) {
        // Assertions
        Assert.notNull(strategyData);
        if(bucketIDs.size()>0) {
            Assert.isTrue(strategyData.getStartTime() > bucketIDs.get(bucketIDs.size()-1).getEndTime());
        }
        BucketData bucketData = new BucketData(strategyData, this);
        addBucketData(bucketData);
        builder.registerInTimerForNextBucketEndTime(bucketData, strategyData.getEndTime());
    }


    /**
     * Updates the end time of the given bucketID.
     * The bucketID MUST be the last bucketID in the bucketIDs list(otherwise it is an error in the program flow).
     * @param bucketID
     * @param endTime
     */
    BucketData setEndTime(String bucketID, Long endTime) {
        Assert.isTrue(bucketIDs.size() > 0);
        BucketData bucketData = bucketIDs.get(bucketIDs.size()-1);
        Assert.isTrue(bucketData.getBucketID() != null && StringUtils.equals(bucketData.getBucketID(), bucketID));
        bucketData.setEndTime(endTime);
        return bucketData;
    }

    void clearOldBucketData() {
        // Cleaning old buckets
        int minBucketToLeave = Math.max(builder.getConf().getBucketsLeap(), builder.getConf().getNumberOfBuckets());
        int largetBucketLeapIndexFoundThatWasSent = -1;
        for (int i = 0; i < bucketIDs.size(); i++) {
            BucketData bucketData = bucketIDs.get(i);
            if(doesItMatchBucketLeap(bucketData)) {
                if(bucketData.wasSentAsLeadingBucket()) {
                    largetBucketLeapIndexFoundThatWasSent=i;
                } else {
                    break;
                }
            }
        }
        int howManyToRemove=largetBucketLeapIndexFoundThatWasSent+1;
        howManyToRemove = bucketIDs.size() - howManyToRemove > minBucketToLeave ? howManyToRemove : bucketIDs.size() - minBucketToLeave;

        for ( int i=0; i < howManyToRemove; i++) {
            AggrFeatureEventData.BucketData bucketData = bucketIDs.remove(0);
            if(bucketData.getBucketID()!=null) {
                builder.getAggrFeatureEventService().removeBucketID2builderMapping(bucketData.getBucketID(), builder);
            }
        }
    }

    class BucketData implements DataSourcesSyncTimerListener {
        private FeatureBucketStrategyData strategyData;
        private String  bucketID;
        private Long startTime;
        private Long endTime;
        private AggrFeatureEventData aggrFeatureEventData;
        private long syncTimerRegistrationID;
        private boolean wasSentAsLeadingBucket;

        public BucketData(FeatureBucketStrategyData strategyData, AggrFeatureEventData aggrFeatureEventData) {
            this.strategyData = strategyData;
            this.aggrFeatureEventData = aggrFeatureEventData;
            this.endTime = strategyData.getEndTime();
        }

        public BucketData(String bucketID, Long startTime, Long endTime, AggrFeatureEventData aggrFeatureEventData) {
            this.bucketID = bucketID;
            this.startTime = startTime;
            this.endTime = endTime;
            this.aggrFeatureEventData = aggrFeatureEventData;
        }

        public boolean wasSentAsLeadingBucket() {
            return wasSentAsLeadingBucket;
        }

        public void setWasSentAsLeadingBucket(boolean wasSentAsLeadingBucket) {
            this.wasSentAsLeadingBucket = wasSentAsLeadingBucket;
        }

        public FeatureBucketStrategyData getStrategyData() {
            return strategyData;
        }

        public String getBucketID() {
            return bucketID;
        }

        public Long getEndTime() {
            return endTime!=null ? endTime : strategyData.getEndTime();
        }

        public Long getStartTime() {
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
    }

}