package com.rsa.netwitness.presidio.automation.domain.output;

import com.google.gson.annotations.Expose;
import org.json.JSONObject;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document(collection = "scored___entity_event__normalized_username_daily")
public class ScoredEntityEventNormalizedUsernameDailyStoredData {

    @Id
    private String id;

    @Expose
    private long start_time_unix;

    @Expose
    private double entity_event_value;

    @Expose
    private double score;

    //    How to handle this inner entities of MongoDB ???
    @Expose
    private List<FeatureScores> feature_scores;
//    private List<JSONObject> feature_scores;

    @Expose
    private double unreduced_score;

    @Expose
    private JSONObject context;

    @Expose
    private String contextId;

    @Expose
    private long end_time_unix;

    @Expose
    private Instant creation_time;

    @Expose
    private long creation_epochtime;

    @Expose
    private String entity_event_type;

    @Expose
    private long date_time_unix;

    @Expose
    private List<JSONObject> aggregated_feature_events;

    @Expose
    private String entity_event_name;

    public long getStart_time_unix() {
        return start_time_unix;
    }

    public void setStart_time_unix(long start_time_unix) {
        this.start_time_unix = start_time_unix;
    }

    public double getEntity_event_value() {
        return entity_event_value;
    }

    public void setEntity_event_value(double entity_event_value) {
        this.entity_event_value = entity_event_value;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public List<FeatureScores> getFeature_score() {
        return feature_scores;
    }

    public void setFeature_score(List<FeatureScores> feature_score) {
        this.feature_scores = feature_score;
    }

    public double getUnreduced_score() {
        return unreduced_score;
    }

    public void setUnreduced_score(double unreduced_score) {
        this.unreduced_score = unreduced_score;
    }

    public JSONObject getContext() {
        return context;
    }

    public void setContext(JSONObject context) {
        this.context = context;
    }

    public String getContextId() {
        return contextId;
    }

    public void setContextId(String contextId) {
        this.contextId = contextId;
    }

    public long getEnd_time_unix() {
        return end_time_unix;
    }

    public void setEnd_time_unix(long end_time_unix) {
        this.end_time_unix = end_time_unix;
    }

    public Instant getCreation_time() {
        return creation_time;
    }

    public void setCreation_time(Instant creation_time) {
        this.creation_time = creation_time;
    }

    public long getCreation_epochtime() {
        return creation_epochtime;
    }

    public void setCreation_epochtime(long creation_epochtime) {
        this.creation_epochtime = creation_epochtime;
    }

    public String getEntity_event_type() {
        return entity_event_type;
    }

    public void setEntity_event_type(String entity_event_type) {
        this.entity_event_type = entity_event_type;
    }

    public long getDate_time_unix() {
        return date_time_unix;
    }

    public void setDate_time_unix(long date_time_unix) {
        this.date_time_unix = date_time_unix;
    }

    public List<JSONObject> getAggregated_feature_events() {
        return aggregated_feature_events;
    }

    public void setAggregated_feature_events(List<JSONObject> aggregated_feature_events) {
        this.aggregated_feature_events = aggregated_feature_events;
    }

    public String getEntity_event_name() {
        return entity_event_name;
    }

    public void setEntity_event_name(String entity_event_name) {
        this.entity_event_name = entity_event_name;
    }
}

class FeatureScores{

    @Expose
    private double certainty;
    @Expose
    private String name;
    @Expose
    private double score;
    @Expose
    private List<FeatureScores> featureScores;

    public double getCertainty() {
        return certainty;
    }

    public void setCertainty(double certainty) {
        this.certainty = certainty;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public List<FeatureScores> getFeatureScores() {
        return featureScores;
    }

    public void setFeatureScores(List<FeatureScores> featureScores) {
        this.featureScores = featureScores;
    }

    public String toString() {
        return "[ " + certainty + ", "
                + name + ", " +
                score + " ]";
    }
}
