package fortscale.streaming.alert.subscribers;

import fortscale.domain.core.EntitySupportingInformation;
import fortscale.domain.core.EntityType;
import fortscale.domain.core.EvidenceType;
import fortscale.streaming.alert.event.wrappers.EnrichedFortscaleEvent;
import net.minidev.json.JSONObject;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shays on 20/03/2016.
 */
public class EnrichedFortscaleEventBuilder {


    final static long TIME_CONSTANT = 1458628635000L; //Tue, 22 Mar 2016 06:37:05 GMT in miliseconds
    final static long MILI_SEC_IN_HOUR = 60 * 60 * 1000;


    //private Map<String, Object> data;

    private EnrichedFortscaleEvent enrichedFortscaleEvent;

    public EnrichedFortscaleEventBuilder(){
        enrichedFortscaleEvent = new EnrichedFortscaleEvent();
        //Set default values, user can override them

        enrichedFortscaleEvent.setId("");
        enrichedFortscaleEvent.setContxtId("user");
        enrichedFortscaleEvent.setScore(50);
        enrichedFortscaleEvent.setEntityName("user@fortscale.com");
        enrichedFortscaleEvent.setEntityType(EntityType.User);
        enrichedFortscaleEvent.setStartTimeUnix(TIME_CONSTANT);
        enrichedFortscaleEvent.setEndTimeUnix(TIME_CONSTANT + MILI_SEC_IN_HOUR);
        enrichedFortscaleEvent.setSupportingInformation(null);
        enrichedFortscaleEvent.setEntityEventType("daily");


    }



    public EnrichedFortscaleEventBuilder setEntityType(EntityType entityType) {
        this.enrichedFortscaleEvent.setEntityType(entityType);
        return this;
    }

    public EnrichedFortscaleEventBuilder setEntityName(String entityName) {
        this.enrichedFortscaleEvent.setEntityName(entityName);
        return this;
    }

    public EnrichedFortscaleEventBuilder setScore(int score) {
        this.enrichedFortscaleEvent.setScore(score);
        return this;
    }

    public EnrichedFortscaleEventBuilder setHourlyStartDate(long hourlyStartDate) {
        this.enrichedFortscaleEvent.setHourlyStartDate(hourlyStartDate);
        return this;
    }

    public EnrichedFortscaleEventBuilder setDailyStartDate(long dailyStartDate) {
        this.enrichedFortscaleEvent.setDailyStartDate(dailyStartDate);
        return this;
    }

    public EnrichedFortscaleEventBuilder setAggregated_feature_events(List<JSONObject> aggregated_feature_events) {
        this.enrichedFortscaleEvent.setAggregatedFeatureEvents(aggregated_feature_events);
        return this;
    }

    public EnrichedFortscaleEventBuilder setStartTimeUnix(long startTimeUnix) {
        this.enrichedFortscaleEvent.setStartTimeUnix(startTimeUnix);
        return this;
    }

    public EnrichedFortscaleEventBuilder setEndTimeUnix(long endTimeUnix) {
        this.enrichedFortscaleEvent.setEndTimeUnix(endTimeUnix);
        return this;
    }

    public EnrichedFortscaleEventBuilder setEntityEventName(String entityEventName) {
        this.enrichedFortscaleEvent.setEntityEventName(entityEventName);
        return this;
    }

    public EnrichedFortscaleEventBuilder setEntityEventType(String entityEventType) {
        this.enrichedFortscaleEvent.setEntityEventType(entityEventType);
        return this;
    }

    public EnrichedFortscaleEventBuilder setContextId(String contextId) {
        this.enrichedFortscaleEvent.setContxtId(contextId);
        return this;
    }

    public EnrichedFortscaleEventBuilder setAnomalyTypeFieldName(String anomalyTypeFieldName) {
        this.enrichedFortscaleEvent.setAnomalyTypeFieldName(anomalyTypeFieldName);
        return this;
    }

    public EnrichedFortscaleEventBuilder setEvidenceType(EvidenceType evidenceType) {
        this.enrichedFortscaleEvent.setEvidenceType(evidenceType);
        return this;
    }

    public EnrichedFortscaleEventBuilder setId(String id) {
        this.enrichedFortscaleEvent.setId(id);
        return this;
    }

    public EnrichedFortscaleEventBuilder setSupportingInformation(EntitySupportingInformation supportingInformation) {
        this.enrichedFortscaleEvent.setSupportingInformation(supportingInformation);
        return this;
    }


    public Map<String, Object> buildMap(){

        Map<String, Object> rowMap = new HashMap<>();

        rowMap.put(EnrichedFortscaleEvent.SUPPORTING_INFORMATION_FIELD_NAME,enrichedFortscaleEvent.getSupportingInformation());
        rowMap.put(EnrichedFortscaleEvent.EVIDENCE_TYPE_FIELD_NAME, enrichedFortscaleEvent.getEvidenceType());
        rowMap.put(EnrichedFortscaleEvent.START_TIME_UNIX_FIELD_NAME,enrichedFortscaleEvent.getStartTimeUnix());
        rowMap.put(EnrichedFortscaleEvent.END_TIME_UNIX_FIELD_NAME,enrichedFortscaleEvent.getEndTimeUnix());
        rowMap.put(EnrichedFortscaleEvent.AGGREGATED_FEATURE_EVENTS_FIELD_NAME,enrichedFortscaleEvent.getAggregatedFeatureEvents());
        rowMap.put(EnrichedFortscaleEvent.ANOMALY_TYPE_FIELD_NAME,enrichedFortscaleEvent.getAnomalyTypeFieldName());
        rowMap.put(EnrichedFortscaleEvent.CONTEXT_ID_FIELD_NAME,enrichedFortscaleEvent.getContxtId());
        rowMap.put(EnrichedFortscaleEvent.DAILY_START_DATE_FIELD_NAME, enrichedFortscaleEvent.getDailyStartDate());
        rowMap.put(EnrichedFortscaleEvent.HOURLY_START_DATE_FIELD_NAME, enrichedFortscaleEvent.getHourlyStartDate());
        rowMap.put(EnrichedFortscaleEvent.ENTITY_EVENT_NAME_FIELD_NAME, enrichedFortscaleEvent.getEntityEventName());
        rowMap.put(EnrichedFortscaleEvent.ENTITY_EVENT_TYPE_FIELD_NAME, enrichedFortscaleEvent.getEntityEventType());
        rowMap.put(EnrichedFortscaleEvent.ID_FIELD_NAME, enrichedFortscaleEvent.getId());
        rowMap.put(EnrichedFortscaleEvent.SCORE_FIELD_NAME, enrichedFortscaleEvent.getScore());
        rowMap.put(EnrichedFortscaleEvent.ENTITY_TYPE_FIELD_NAME, enrichedFortscaleEvent.getEntityType());
        rowMap.put(EnrichedFortscaleEvent.ENTITY_NAME_FIELD_NAME, enrichedFortscaleEvent.getEntityName());
        rowMap.put(EnrichedFortscaleEvent.SUPPORTING_INFORMATION_FIELD_NAME, enrichedFortscaleEvent.getSupportingInformation());
        rowMap.put(EnrichedFortscaleEvent.DATA_ENTITY_IDS_FIELD_NAME, enrichedFortscaleEvent.getDataEntitiesIds());

        return rowMap;
    }

    public EnrichedFortscaleEvent buildObject(){

        return  this.enrichedFortscaleEvent;
    }
}
