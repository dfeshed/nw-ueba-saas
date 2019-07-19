package com.rsa.netwitness.presidio.automation.domain.output;

import com.google.gson.annotations.Expose;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.Arrays;


@Document(collection = "smart_userId_hourly")
public class SmartUserIdStoredRecored {

    @Id
    private String id;

    @Expose
    private String fixedDurationStrategy;

    @Expose
    private Double smartValue;

    @Expose
    @Field("smartScore")
    private Double smartScore;

    @Expose
    private String featureName;

    @Expose
    private Context context;

    @Expose
    private String contextId;

    @Expose
    private Instant startInstant;

    @Expose
    @Field("smartAggregationRecords")
    private smartAggregationRecords[] smartAggregationRecords;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFixedDurationStrategy() {
        return fixedDurationStrategy;
    }

    public void setFixedDurationStrategy(String fixedDurationStrategy) {
        this.fixedDurationStrategy = fixedDurationStrategy;
    }

    public Double getSmartValue() {
        return smartValue;
    }

    public void setSmartValue(Double smartValue) {
        this.smartValue = smartValue;
    }

    public Double getSmartScore() {
        return smartScore;
    }

    public void setSmartScore(Double smartScore) {
        this.smartScore = smartScore;
    }

    public String getFeatureName() {
        return featureName;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getContextId() {
        return contextId;
    }

    public void setContextId(String contextId) {
        this.contextId = contextId;
    }

    public smartAggregationRecords[] getSmartAggregationRecords() {
        return smartAggregationRecords;
    }

    public void setSmartAggregationRecords(smartAggregationRecords[] aggregationRecords) {
        this.smartAggregationRecords = aggregationRecords;
    }

    public Instant getStartInstant() {
        return startInstant;
    }

    public void setStartInstant(Instant startInstant) {
        this.startInstant = startInstant;
    }

    @Override
    public String toString() {
        return "SmartUserIdStoredRecored{" +
                "id='" + id + '\'' +
                ", fixedDurationStrategy='" + fixedDurationStrategy + '\'' +
                ", smartValue=" + smartValue +
                ", smartScore=" + smartScore +
                ", featureName='" + featureName + '\'' +
                ", context=" + context +
                ", contextId='" + contextId + '\'' +
                ", startInstant=" + startInstant +
                ", aggregationRecords=" + Arrays.toString(smartAggregationRecords) +
                '}';
    }

    public class Context{

        private String userId;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        @Override
        public String toString() {
            return "Context{" +
                    "userId='" + userId + '\'' +
                    '}';
        }
    }

    public class Aggregation {

        public Aggregation(Double score, String featureName, Double featureValue, String featureBucketConfName, Context context, String aggregatedFeatureType){
            this.score = score;
            this.featureName = featureName;
            this.featureValue = featureValue;
            this.featureBucketConfName = featureBucketConfName;
            this.aggregatedFeatureType = aggregatedFeatureType;
        }

        private Double score;

        private String featureName;

        private Double featureValue;

        private String featureBucketConfName;

        private Context context;

        private String aggregatedFeatureType;

        public Double getScore() {
            return score;
        }

        public void setScore(Double score) {
            this.score = score;
        }

        public String getFeatureName() {
            return featureName;
        }

        public void setFeatureName(String featureName) {
            this.featureName = featureName;
        }

        public Double getFeatureValue() {
            return featureValue;
        }

        public void setFeatureValue(Double featureValue) {
            this.featureValue = featureValue;
        }

        public String getFeatureBucketConfName() {
            return featureBucketConfName;
        }

        public void setFeatureBucketConfName(String featureBucketConfName) {
            this.featureBucketConfName = featureBucketConfName;
        }

        public Context getContext() {
            return context;
        }

        public void setContext(Context context) {
            this.context = context;
        }

        public String getAggregatedFeatureType() {
            return aggregatedFeatureType;
        }

        public void setAggregatedFeatureType(String aggregatedFeatureType) {
            this.aggregatedFeatureType = aggregatedFeatureType;
        }

        @Override
        public String toString() {
            return "aggregationRecords{" +
                    "score=" + score +
                    ", featureName='" + featureName + '\'' +
                    ", featureValue=" + featureValue +
                    ", featureBucketConfName='" + featureBucketConfName + '\'' +
                    ", context=" + context +
                    ", aggregatedFeatureType='" + aggregatedFeatureType + '\'' +
                    '}';
        }
    }

    public class smartAggregationRecords {
        Aggregation aggregationRecord;
        Double contribution;
        Double scoreAndWeightProduct;

        public smartAggregationRecords(Aggregation aggregationRecord, Double contribution, Double scoreAndWeightProduct){
            this.aggregationRecord = aggregationRecord;
            this.contribution = contribution;
            this.scoreAndWeightProduct = scoreAndWeightProduct;
        }

        public Aggregation getAggregationRecord() {
            return aggregationRecord;
        }

        public void setAggregationRecord(Aggregation aggregationRecord) {
            this.aggregationRecord = aggregationRecord;
        }

        public Double getContribution() {
            return contribution;
        }

        public void setContribution(Double contribution) {
            this.contribution = contribution;
        }

        public Double getScoreAndWeightProduct() {
            return scoreAndWeightProduct;
        }

        public void setScoreAndWeightProduct(Double scoreAndWeightProduct) {
            this.scoreAndWeightProduct = scoreAndWeightProduct;
        }

        @Override
        public String toString() {
            return "smartAggregationRecords{" +
                    "aggregationRecord=" + aggregationRecord +
                    ", contribution=" + contribution +
                    ", scoreAndWeightProduct=" + scoreAndWeightProduct +
                    '}';
        }
    }
}


