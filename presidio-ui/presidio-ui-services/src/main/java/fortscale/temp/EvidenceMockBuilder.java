package fortscale.temp;

import fortscale.domain.core.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by shays on 05/07/2017.
 */
public class EvidenceMockBuilder {


    private String id;


    private EntityType entityType;

    private String entityTypeFieldName;

    private String entityName;


    private Long startDate;


    private Long endDate;


    private Date retentionDate;


    private String anomalyType;

    private String anomalyTypeFieldName;

    private String name;

    private String anomalyValue;

    private List<String> dataEntitiesIds;

    private EvidenceType evidenceType;


    private Integer score;


    private Severity severity;


    private Integer numOfEvents;

    private EvidenceTimeframe timeframe;


    public EvidenceMockBuilder(int serial){
        this.id=id+1;
        this.name="alert"+serial;
        this.score = HardCodedMocks.DEFAULT_SCORE;
        this.severity = Severity.Medium;
        this.setAnomalyTypeFieldName("failure_code");
        this.setAnomalyType("failure_code");
        this.setEvidenceType(EvidenceType.AnomalySingleEvent);
        this.dataEntitiesIds = Arrays.asList("ssh");
        this.setAnomalyValue("0x22");
        this.setNumOfEvents(1);
        this.setEntityType(EntityType.User);
        this.setEntityName("secusr16@somebigcompany.com");
    }

    public EvidenceMockBuilder setEntityType(EntityType entityType) {
        this.entityType = entityType;
        return  this;
    }

    public EvidenceMockBuilder setEntityTypeFieldName(String entityTypeFieldName) {
        this.entityTypeFieldName = entityTypeFieldName;
        return  this;
    }

    public EvidenceMockBuilder setEntityName(String entityName) {
        this.entityName = entityName;
        return  this;
    }

    public EvidenceMockBuilder setStartDate(Long startDate) {
        this.startDate = startDate;
        return  this;
    }

    public EvidenceMockBuilder setEndDate(Long endDate) {
        this.endDate = endDate;
        return  this;
    }

    public EvidenceMockBuilder setRetentionDate(Date retentionDate) {
        this.retentionDate = retentionDate;
        return  this;
    }

    public EvidenceMockBuilder setAnomalyType(String anomalyType) {
        this.anomalyType = anomalyType;
        return  this;
    }

    public EvidenceMockBuilder setAnomalyTypeFieldName(String anomalyTypeFieldName) {
        this.anomalyTypeFieldName = anomalyTypeFieldName;
        return  this;
    }

    public EvidenceMockBuilder setName(String name) {
        this.name = name;
        return  this;
    }

    public EvidenceMockBuilder setAnomalyValue(String anomalyValue) {
        this.anomalyValue = anomalyValue;
        return  this;
    }

    public EvidenceMockBuilder setDataEntitiesIds(List<String> dataEntitiesIds) {
        this.dataEntitiesIds = dataEntitiesIds;
        return  this;
    }

    public EvidenceMockBuilder setEvidenceType(EvidenceType evidenceType) {
        this.evidenceType = evidenceType;
        return  this;
    }

    public EvidenceMockBuilder setScore(Integer score) {
        this.score = score;
        return  this;
    }

    public EvidenceMockBuilder setSeverity(Severity severity) {
        this.severity = severity;
        return  this;
    }

    public EvidenceMockBuilder setNumOfEvents(Integer numOfEvents) {
        this.numOfEvents = numOfEvents;
        return  this;
    }

    public EvidenceMockBuilder setTimeframe(EvidenceTimeframe timeframe) {
        this.timeframe = timeframe;
        return  this;
    }

    public Evidence createInstance(){
        Evidence evidence = new Evidence();

        evidence.setMockId(this.id);
        evidence.setName(this.name);

        evidence.setScore (this.score);
        evidence.setSeverity(this.severity);
        evidence.setEndDate(endDate);
        evidence.setStartDate(startDate);
        evidence.setAnomalyType(anomalyType);
        evidence.setAnomalyValue(anomalyValue);
        evidence.setDataEntitiesIds(dataEntitiesIds);
        evidence.setEvidenceType(evidenceType) ;
        evidence.setAnomalyTypeFieldName(anomalyTypeFieldName);
        evidence.setTimeframe(timeframe);
        evidence.setNumOfEvents(numOfEvents);


        return evidence;
    }
}
