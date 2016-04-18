package fortscale.streaming.alert.event.wrappers;

import fortscale.domain.core.*;
import fortscale.domain.core.EvidenceType;
import net.minidev.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Created by shays on 20/03/2016.
 */
public class EnrichedFortscaleEvent {

    public final static String ENTITY_TYPE_FIELD_NAME = "entityType";
    public final static String ENTITY_NAME_FIELD_NAME = "entityName";
    public final static String SCORE_FIELD_NAME = "score";
    public final static String HOURLY_START_DATE_FIELD_NAME = "hourlyStartDate";
    public final static String DAILY_START_DATE_FIELD_NAME = "dailyStartDate";
    public final static String AGGREGATED_FEATURE_EVENTS_FIELD_NAME = "aggregated_feature_events";
    public final static String START_TIME_UNIX_FIELD_NAME = "startDate";
    public final static String END_TIME_UNIX_FIELD_NAME = "endDate";
    public final static String ENTITY_EVENT_NAME_FIELD_NAME = "entity_event_name";
    public final static String ENTITY_EVENT_TYPE_FIELD_NAME = "entity_event_type";
    public final static String CONTEXT_ID_FIELD_NAME = "contextId";
    public final static String ANOMALY_TYPE_FIELD_NAME = "anomalyTypeFieldName";
    public final static String EVIDENCE_TYPE_FIELD_NAME = "evidenceType";
    public final static String ID_FIELD_NAME = "id";
    public final static String SUPPORTING_INFORMATION_FIELD_NAME = "supportingInformation";


    private String id;
    private EvidenceType EvidenceType;
    private EntityType entityType;
    private String entityName;
    private int score;
    private long hourlyStartDate;
    private long dailyStartDate;
    private long startTimeUnix;
    private long endTimeUnix;
    private List<JSONObject> aggregatedFeatureEvents;
    private String entityEventName;
    private String entityEventType;
    private String contxtId;


    private EntitySupportingInformation supportingInformation;
    private String anomalyTypeFieldName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public EvidenceType getEvidenceType() {
        return EvidenceType;
    }

    public void setEvidenceType(EvidenceType EvidenceType) {
        this.EvidenceType = EvidenceType;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public void setEntityType(EntityType entityType) {
        this.entityType = entityType;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public long getHourlyStartDate() {
        return hourlyStartDate;
    }

    public void setHourlyStartDate(long hourlyStartDate) {
        this.hourlyStartDate = hourlyStartDate;
    }

    public long getDailyStartDate() {
        return dailyStartDate;
    }

    public void setDailyStartDate(long dailyStartDate) {
        this.dailyStartDate = dailyStartDate;
    }

    public List<JSONObject> getAggregatedFeatureEvents() {
        return aggregatedFeatureEvents;
    }

    public void setAggregatedFeatureEvents(List<JSONObject> aggregatedFeatureEvents) {
        this.aggregatedFeatureEvents = aggregatedFeatureEvents;
    }

    public String getAnomalyTypeFieldName() {
        return anomalyTypeFieldName;
    }

    public void setAnomalyTypeFieldName(String anomalyTypeFieldName) {
        this.anomalyTypeFieldName = anomalyTypeFieldName;
    }

    public EntitySupportingInformation getSupportingInformation() {
        return supportingInformation;
    }

    public void setSupportingInformation(EntitySupportingInformation supportingInformation) {
        this.supportingInformation = supportingInformation;
    }

    public long getStartTimeUnix() {
        return startTimeUnix;
    }

    public void setStartTimeUnix(long startTimeUnix) {
        this.startTimeUnix = startTimeUnix;
    }

    public long getEndTimeUnix() {
        return endTimeUnix;
    }

    public void setEndTimeUnix(long endTimeUnix) {
        this.endTimeUnix = endTimeUnix;
    }

    public String getEntityEventName() {
        return entityEventName;
    }

    public void setEntityEventName(String entityEventName) {
        this.entityEventName = entityEventName;
    }

    public String getEntityEventType() {
        return entityEventType;
    }

    public void setEntityEventType(String entityEventType) {
        this.entityEventType = entityEventType;
    }

    public String getContxtId() {
        return contxtId;
    }

    public void setContxtId(String contxtId) {
        this.contxtId = contxtId;
    }

    public void fromMap(Map map){

        this.setAggregatedFeatureEvents((List<JSONObject>)map.get(AGGREGATED_FEATURE_EVENTS_FIELD_NAME));
        this.setEntityEventType((String)map.get(ENTITY_EVENT_TYPE_FIELD_NAME));
        this.setEntityName((String)map.get(ENTITY_NAME_FIELD_NAME));

        this.setId((String)map.get(ID_FIELD_NAME));
        this.setScore((Integer)map.get(SCORE_FIELD_NAME));
        this.setAnomalyTypeFieldName((String)map.get(ANOMALY_TYPE_FIELD_NAME));
        this.setEntityType((EntityType)map.get(ENTITY_TYPE_FIELD_NAME));

        this.setEntityEventType((String)map.get(ENTITY_EVENT_TYPE_FIELD_NAME));
        this.setEntityEventName((String)map.get(ENTITY_EVENT_NAME_FIELD_NAME));
        this.setContxtId((String)map.get(CONTEXT_ID_FIELD_NAME));
        this.setDailyStartDate((Long)map.get(DAILY_START_DATE_FIELD_NAME));

        this.setHourlyStartDate((Long)map.get(HOURLY_START_DATE_FIELD_NAME));
        this.setStartTimeUnix((Long)map.get(START_TIME_UNIX_FIELD_NAME));
        this.setEndTimeUnix((Long)map.get(END_TIME_UNIX_FIELD_NAME));


        this.setEvidenceType((EvidenceType)map.get(EVIDENCE_TYPE_FIELD_NAME));
        this.setSupportingInformation((EntitySupportingInformation)map.get(SUPPORTING_INFORMATION_FIELD_NAME));



    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EnrichedFortscaleEvent)) return false;

        EnrichedFortscaleEvent that = (EnrichedFortscaleEvent) o;

        if (score != that.score) return false;
        if (hourlyStartDate != that.hourlyStartDate) return false;
        if (dailyStartDate != that.dailyStartDate) return false;
        if (startTimeUnix != that.startTimeUnix) return false;
        if (endTimeUnix != that.endTimeUnix) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (EvidenceType != that.EvidenceType) return false;
        if (entityType != that.entityType) return false;
        if (entityName != null ? !entityName.equals(that.entityName) : that.entityName != null) return false;
        if (aggregatedFeatureEvents != null ? !aggregatedFeatureEvents.equals(that.aggregatedFeatureEvents) : that.aggregatedFeatureEvents != null)
            return false;
        if (entityEventName != null ? !entityEventName.equals(that.entityEventName) : that.entityEventName != null)
            return false;
        if (entityEventType != null ? !entityEventType.equals(that.entityEventType) : that.entityEventType != null)
            return false;
        if (contxtId != null ? !contxtId.equals(that.contxtId) : that.contxtId != null) return false;
        if (supportingInformation != null ? !supportingInformation.equals(that.supportingInformation) : that.supportingInformation != null)
            return false;
        return !(anomalyTypeFieldName != null ? !anomalyTypeFieldName.equals(that.anomalyTypeFieldName) : that.anomalyTypeFieldName != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (EvidenceType != null ? EvidenceType.hashCode() : 0);
        result = 31 * result + (entityType != null ? entityType.hashCode() : 0);
        result = 31 * result + (entityName != null ? entityName.hashCode() : 0);
        result = 31 * result + score;
        result = 31 * result + (int) (hourlyStartDate ^ (hourlyStartDate >>> 32));
        result = 31 * result + (int) (dailyStartDate ^ (dailyStartDate >>> 32));
        result = 31 * result + (int) (startTimeUnix ^ (startTimeUnix >>> 32));
        result = 31 * result + (int) (endTimeUnix ^ (endTimeUnix >>> 32));
        result = 31 * result + (aggregatedFeatureEvents != null ? aggregatedFeatureEvents.hashCode() : 0);
        result = 31 * result + (entityEventName != null ? entityEventName.hashCode() : 0);
        result = 31 * result + (entityEventType != null ? entityEventType.hashCode() : 0);
        result = 31 * result + (contxtId != null ? contxtId.hashCode() : 0);
        result = 31 * result + (supportingInformation != null ? supportingInformation.hashCode() : 0);
        result = 31 * result + (anomalyTypeFieldName != null ? anomalyTypeFieldName.hashCode() : 0);
        return result;
    }
}
