package fortscale.streaming.service.aggregation.feature.event;

import fortscale.streaming.service.aggregation.DataSourcesSyncTimerListener;
import fortscale.streaming.service.aggregation.bucket.strategy.NextBucketEndTimeListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class AggrFeatureEventData implements DataSourcesSyncTimerListener, NextBucketEndTimeListener {
    AggrFeatureEventBuilder builder;
    List<String> bucketIDs;
    Map<String, String> context;
    Long startTime;
    Long endTime;
    long syncTimerRegistrationID;

    AggrFeatureEventData(AggrFeatureEventBuilder builder, Map<String, String> context, Long startTime, Long endTime) {
        this.builder = builder;
        this.context = context;
        this.startTime = startTime;
        this.endTime = endTime;
        bucketIDs = new ArrayList<>();
    }

    Long getStartTime() {
        return startTime;
    }

    void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    Long getEndTime() {
        return endTime;
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

    void addBucketID(String bucketID) {
        bucketIDs.add(bucketID);
    }

    List<String> getBucketIDs() {
        return bucketIDs;
    }

    @Override
    public void nextBucketEndTimeUpdate(Long time) {
        builder.registerInTimerForNextBucketEndTime(this, time);
    }
}