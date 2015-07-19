package fortscale.streaming.service.aggregation.feature.event;

import fortscale.streaming.service.aggregation.DataSourcesSyncTimerListener;
import fortscale.streaming.service.aggregation.bucket.strategy.FeatureBucketStrategyData;
import fortscale.streaming.service.aggregation.bucket.strategy.NextBucketEndTimeListener;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * AggrFeatureEventData maintains a list of bucketIDs and other information that is used by the associated
 * AggrFeatureEventBuilder to generate Aggregated Feature Events of a specific type for a specific context.
 */
class AggrFeatureEventData implements DataSourcesSyncTimerListener, NextBucketEndTimeListener {
    private AggrFeatureEventBuilder builder;
    private List<BucketData> bucketIDs;
    private Map<String, String> context;

    // The registrationID returned by the syncTimer at the latest registration of this object
    private long syncTimerRegistrationID;

    // The number of buckets between two consequent events
    private int bucketsLeap;

    // Counts the number of buckets left to next event based on the
    // bucketsLeap value
    private int numberOfBucketsToWaitBeforeSendingNextEvent;

    public BucketData removeFirstBucketData() {
        return bucketIDs.size()>0 ? bucketIDs.remove(0) : null;
    }

    class BucketData {
        private FeatureBucketStrategyData strategyData;
        private String  bucketID;
        private Long startTime;
        private Long endTime;

        public BucketData(FeatureBucketStrategyData strategyData) {
            this.strategyData = strategyData;
        }

        public BucketData(String bucketID, Long startTime, Long endTime) {
            this.bucketID = bucketID;
            this.startTime = startTime;
            this.endTime = endTime;
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
    }

    /**
     *
     * @param builder
     * @param context
     * @param bucketsLeap must be greater then zero
     */
    AggrFeatureEventData(@NotNull AggrFeatureEventBuilder builder, @NotNull Map<String, String> context, int bucketsLeap) {
        Assert.notNull(builder);
        Assert.notNull(context);
        Assert.isTrue(bucketsLeap>0);

        this.builder = builder;
        this.context = context;
        this.bucketIDs = new ArrayList<>();
        this.bucketsLeap = bucketsLeap;
        this.numberOfBucketsToWaitBeforeSendingNextEvent = this.bucketsLeap;
    }

    Map<String, String> getContext() {
        return context;
    }

    AggrFeatureEventBuilder getBuilder() {
        return builder;
    }

    void setSyncTimerRegistrationID(long syncTimerRegistrationID) {
        this.syncTimerRegistrationID = syncTimerRegistrationID;
    }

    long getSyncTimerRegistrationID() {
        return syncTimerRegistrationID;
    }

    @Override
    public void dataSourcesReachedTime() {
        builder.dataSourcesReachedTime(this);
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
    void addBucketID(@NotNull String bucketID, Long startTime, Long endTime) {
        // Assertions
        Assert.isTrue(endTime > startTime && startTime > 0);
        Assert.notNull(bucketID);
        Assert.isTrue(StringUtils.isNotEmpty(bucketID));

        //check if the bucketID is related to any strategyData already in the list
        boolean found = false;
        if(bucketIDs.size()>0) {
            BucketData bucketData = bucketIDs.get(bucketIDs.size()-1);

            // If the last item in the list contains strategy ID and not bucket ID
            // then the new bucket must batch that strategy otherwise there is an
            // error in the program flow.
            if(bucketData.strategyData!=null && bucketData.getBucketID()==null) {
                String strategyID = bucketData.strategyData.getStrategyId();

                Assert.isTrue(StringUtils.startsWith(bucketID, strategyID));
                Assert.isTrue(bucketData.strategyData.getStartTime() == startTime &&
                              bucketData.strategyData.getEndTime() == endTime);

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
            BucketData bucketData = new BucketData(bucketID, startTime, endTime);
            addBucketData(bucketData);
        }
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
        numberOfBucketsToWaitBeforeSendingNextEvent--;
    }

    /**
     * Adds the strategyData to the bucketIDs list.
     * The bucket start time MUST be later the the end time of the last bucket in the list.
     * @param strategyData
     */
    @Override
    public void nextBucketEndTimeUpdate(@NotNull FeatureBucketStrategyData strategyData) {
        // Assertions
        Assert.notNull(strategyData);
        if(bucketIDs.size()>0) {
            Assert.isTrue(strategyData.getStartTime() > bucketIDs.get(bucketIDs.size()-1).getEndTime());
        }
        BucketData bucketData = new BucketData(strategyData);
        addBucketData(bucketData);
        builder.registerInTimerForNextBucketEndTime(this, strategyData.getEndTime());
    }

    int getNumberOfBucketsToWaitBeforeSendingNextEvent() {
        return numberOfBucketsToWaitBeforeSendingNextEvent;
    }

    int getBucketsLeap() {
        return bucketsLeap;
    }

    void updateNumberOfBucketsToWaitBeforeSendingNextEvent() {
        numberOfBucketsToWaitBeforeSendingNextEvent=bucketsLeap;
    }

    /**
     * Updates the end time of the given bucketID.
     * The bucketID MUST be the last bucketID in the bucketIDs list(otherwise it is an error in the program flow).
     * @param bucketID
     * @param endTime
     */
    void setEndTime(String bucketID, Long endTime) {
        Assert.isTrue(bucketIDs.size()>0);
        BucketData bucketData = bucketIDs.get(bucketIDs.size()-1);
        Assert.isTrue(bucketData.getBucketID() != null && StringUtils.equals(bucketData.getBucketID(), bucketID) );
        bucketData.setEndTime(endTime);
    }
}