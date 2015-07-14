package fortscale.streaming.service.aggregation.feature.event;

import fortscale.streaming.service.aggregation.DataSourcesSyncTimerListener;
import fortscale.streaming.service.aggregation.bucket.strategy.FeatureBucketStrategyData;
import fortscale.streaming.service.aggregation.bucket.strategy.NextBucketEndTimeListener;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class AggrFeatureEventData implements DataSourcesSyncTimerListener, NextBucketEndTimeListener {
    private AggrFeatureEventBuilder builder;
    private List<BucketData> bucketIDs;
    private Map<String, String> context;

    private long syncTimerRegistrationID;
    private int numberOfBucketsToWaitBeforeSendingNextEvent;
    private int bucketsLeap;

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

    AggrFeatureEventData(AggrFeatureEventBuilder builder, Map<String, String> context, int bucketsLeap) {
        this.builder = builder;
        this.context = context;
        this.bucketIDs = new ArrayList<>();
        this.bucketsLeap = (bucketsLeap < 1) ? 1: bucketsLeap;
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
    public void dataSourcesReachedTime(long time) {
        builder.dataSourcesReachedTime(this);
    }

    void addBucketID(String bucketID, Long startTime, Long endTime) {
        //check if the bucketID is related to any strategyData already in the list
        boolean found = false;
        for(BucketData bucketData: bucketIDs) {
            String strategyID = bucketData.strategyData.getStrategyId();
            if(strategyID!=null && StringUtils.startsWith(bucketID, strategyID)) {
                bucketData.bucketID = bucketID;
                found = true;
                break;
            }
        }
        if(!found) {
            BucketData bucketData = new BucketData(bucketID, startTime, endTime);
            addBucketData(bucketData);
        }
    }

    List<BucketData> getBucketIDs() {
        return bucketIDs;
    }

    private void addBucketData(BucketData bucketData) {
        Assert.notNull(bucketData);
        bucketIDs.add(bucketData);
        numberOfBucketsToWaitBeforeSendingNextEvent--;
    }

    @Override
    public void nextBucketEndTimeUpdate(FeatureBucketStrategyData strategyData) {
        if(strategyData.getEndTime() > bucketIDs.get(bucketIDs.size()-1).getEndTime()) {
            BucketData bucketData = new BucketData(strategyData);
            addBucketData(bucketData);
            builder.registerInTimerForNextBucketEndTime(this, strategyData.getEndTime());
        }
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

    void setEndTime(String bucketID, Long endTime) {
        boolean found = false;
        for(BucketData bucketData : bucketIDs) {
            if(bucketData.getBucketID() != null && StringUtils.equals(bucketData.getBucketID(), bucketID) ) {
                bucketData.setEndTime(endTime);
                found = true;
                break;
            }
        }
        Assert.isTrue(found);
    }
}