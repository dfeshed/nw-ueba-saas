package fortscale.domain.core;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Represents single evidence in MongoDB
 *
 * More information: https://fortscale.atlassian.net/wiki/display/FSC/Evidence+Collection+in+MongoDB
 *
 * Date: 6/22/2015.
 */
@Document(collection = Evidence.COLLECTION_NAME)
@CompoundIndexes({
		// index for getting all evidences for specific user
	@CompoundIndex(name="entity_idx", def = "{'" + Evidence.entityNameField + "': 1, '" + Evidence.entityTypeField +"': 1}", unique = false),
		// index for making sure our evidence is unique
	@CompoundIndex(name="new_unique_evidence", def = "{'" + Evidence.startDateField + "': 1, '" + Evidence.endDateField +"': 1, '" + Evidence.entityTypeField +"': 1, '" + Evidence.entityNameField +"': 1, '" + Evidence.anomalyTypeFieldNameField +"': 1, '"+ Evidence.anomalyValueField +"': 1}", unique = true)
})
@JsonIgnoreProperties(ignoreUnknown = true)
public class Evidence extends AbstractDocument{

	/**
	 * Collection Name
	 */
	public static final String COLLECTION_NAME = "evidences";


	//-- Document's Field Names


	// Entity information
	public static final String entityTypeField = "entityType";

	public static final String entityNameField = "entityName";

	public static final String entityTypeFieldNameField = "entityTypeFieldName";

	// Time frame information
	public static final String startDateField = "startDate";
	public static final String endDateField = "endDate";
	public static final String retentionDateField = "retentionDate";

	public static final String anomalyTypeFieldNameField = "anomalyTypeFieldName";
	// attributes
	public static final String anomalyValueField = "anomalyValue";
	public static final String dataEntityIdField = "dataEntitiesIds";
	public static final String evidenceTypeField = "evidenceType";

	// The 3 top events
	public static final String top3eventsField = "top3eventsJsonStr";
	// number of events in evidence
	public static final String numOfEventsField = "numOfEvents";
	// supporting Information
	public static final String supportingInformationField = "supportingInformation";

	public static final String timeframeField = "timeframe";

	// severity and score
	public static final String scoreField = "score";
	public static final String severityField = "severity";




	//-- Document's Fields


	@Field(entityTypeField)
	private EntityType entityType;

	//used for mapping between the entityType and the field name in the event
	private String entityTypeFieldName;

	@Field(entityNameField)
	private String entityName;

	@Field(startDateField)
	private Long startDate;

	@Indexed
	@Field(endDateField)
	private Long endDate;

	// Index for expiration (TTL): one year
	@Indexed(expireAfterSeconds = 31536000)
	@Field(retentionDateField)
	private Date retentionDate;

//	@Transient
//	private String anomalyType;

	//used for mapping between the anomalyType and the field name in the event
	private String anomalyTypeFieldName;

	@Transient
	private String name;

	@Field(anomalyValueField)
	private String anomalyValue;

	@Field(dataEntityIdField)
	private List<String> dataEntitiesIds;

	@Field(evidenceTypeField)
	private EvidenceType evidenceType;

	@Field(scoreField)
	private Integer score;

	@Field(severityField)
	private Severity severity;

	@Field(top3eventsField)
	private String top3eventsJsonStr;

	// keeping the events as map - not kept in MongoDB - using for alert (if need to query other event properties as part of the rule)
	@Transient
	private Map<String,Object>[] top3events;

	@Field(numOfEventsField)
	private Integer numOfEvents;

	@Field(timeframeField)
	private EvidenceTimeframe timeframe;


	@JsonInclude
	@Field(supportingInformationField)
	private EntitySupportingInformation supportingInformation;

	// C-tor

	public Evidence (Evidence evidence) {
		this.entityType = evidence.getEntityType();
		this.entityTypeFieldName = evidence.getEntityTypeFieldName();
		this.entityName = evidence.getEntityName();
		this.evidenceType = evidence.getEvidenceType();
		this.numOfEvents = evidence.getNumOfEvents();
		this.startDate = evidence.getStartDate();
		this.endDate = evidence.getEndDate();
		this.anomalyTypeFieldName = evidence.getAnomalyTypeFieldName();
		this.anomalyValue = evidence.getAnomalyValue();
		this.dataEntitiesIds = evidence.getDataEntitiesIds();
		this.score = evidence.getScore();
		this.severity = evidence.getSeverity();
		this.timeframe = evidence.getTimeframe();
		// set retention to start date
		this.retentionDate = new Date(startDate);
		// We must create ID for the evidence so the alert can have reference to it
		this.setId(evidence.getId());
	}

	public Evidence(EntityType entityType, String entityTypeFieldName, String entityName, EvidenceType evidenceType, Long startDate, Long endDate, String anomalyTypeFieldName,
			String anomalyValue, List<String> dataEntitiesIds, Integer score, Severity severity,Integer totalAmountOfEvents, EvidenceTimeframe timeframe) {
		this.entityType = entityType;
		this.entityTypeFieldName = entityTypeFieldName;
		this.entityName = entityName;
		this.evidenceType = evidenceType;
		if (evidenceType == EvidenceType.AnomalySingleEvent) {
			this.numOfEvents = 1;
		} else {
            if (totalAmountOfEvents != null)
                this.numOfEvents = totalAmountOfEvents;
            else
			    this.numOfEvents = -1;
		}
		this.startDate = startDate;
		this.endDate = endDate;
		this.anomalyTypeFieldName = anomalyTypeFieldName;
		this.anomalyValue = anomalyValue;
		this.dataEntitiesIds = dataEntitiesIds;
		this.score = score;
		this.severity = severity;

		this.timeframe = timeframe;


		// set retention to start date
		this.retentionDate = new Date(startDate);

		// We must create ID for the evidence so the alert can have reference to it
		this.setId(UUID.randomUUID().toString());
	}

	// used to create references to evidences within alerts (see BasicAlertSubscriber)
	public Evidence(String id) {
		this.setId(id);
	}

	// For JSON serialization
	public Evidence() {
	}

	// Setters

	public void setRetentionDate(Date retentionDate) {
		this.retentionDate = retentionDate;
	}

	public void setTop3eventsJsonStr(String top3eventsJsonStr) {
		this.top3eventsJsonStr = top3eventsJsonStr;
	}

	public void setNumOfEvents(Integer numOfEvents) {
		this.numOfEvents = numOfEvents;
	}

	public void setEvidenceType(EvidenceType evidenceType) {
		this.evidenceType = evidenceType;
	}

	public void setTop3events(Map<String, Object>[] top3events) {
		this.top3events = top3events;
	}

	public void setDataEntitiesIds(List<String> dataEntitiesIds) {
		this.dataEntitiesIds = dataEntitiesIds;
	}

	public void setName(String name) {
		this.name = name;
	}

//	public void setAnomalyType(String anomalyType) {
//		this.anomalyType = anomalyType;
//	}



	// Getters

	public EntityType getEntityType() {
		return entityType;
	}

	public String getEntityTypeFieldName() {
		return entityTypeFieldName;
	}

	public String getEntityName() {
		return entityName;
	}

	public Long getStartDate() {
		return startDate;
	}

	public void setStartDate(Long startDate) {
		this.startDate = startDate;
	}

	public Long getEndDate() {
		return endDate;
	}

	public void setEndDate(Long endDate) {
		this.endDate = endDate;
	}

	public Date getRetentionDate() {
		return retentionDate;
	}

//	public String getAnomalyType() {
//		return anomalyType;
//	}


    public void setAnomalyTypeFieldName(String anomalyTypeFieldName) {
        this.anomalyTypeFieldName = anomalyTypeFieldName;
    }

    public String getAnomalyTypeFieldName() {
		return anomalyTypeFieldName;
	}

	public String getName() {
		return name;
	}

	public Integer getScore() {
		return score;
	}

	public Severity getSeverity() {
		return severity;
	}

	public List<String> getDataEntitiesIds() {
		return dataEntitiesIds;
	}

	public void setSupportingInformation(EntitySupportingInformation supportingInformationData){
		this.supportingInformation = supportingInformationData;
	}

	public EntitySupportingInformation getSupportingInformation() {
		return supportingInformation;
	}

	public String getTop3eventsJsonStr() {
		return top3eventsJsonStr;
	}

	public String getAnomalyValue() {
		return anomalyValue;
	}

	public void setAnomalyValue(String anomalyValue) {
		this.anomalyValue = anomalyValue;
	}

	public Integer getNumOfEvents() {
		return numOfEvents;
	}

	public EvidenceType getEvidenceType() {
		return evidenceType;
	}

	public Map<String, Object>[] getTop3events() {
		return top3events;
	}

	public EvidenceTimeframe getTimeframe() {
		return timeframe;
	}

	public void setTimeframe(EvidenceTimeframe timeframe) {
		this.timeframe = timeframe;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		if (this.entityType == null && entityName == null && startDate == null && endDate == null
				&& anomalyTypeFieldName == null && evidenceType == null) {
			// if all data members composing the key of the evidence are null, compare the ID by the super method.
			// this case is used when we have a reference to an existing evidence.
			return super.equals(o);
		}

		Evidence evidence = (Evidence) o;

		if (entityType != evidence.entityType) return false;
		if (entityName != null ? !entityName.equals(evidence.entityName) : evidence.entityName != null) return false;
		if (startDate != null ? !startDate.equals(evidence.startDate) : evidence.startDate != null) return false;
		if (endDate != null ? !endDate.equals(evidence.endDate) : evidence.endDate != null) return false;
		if (anomalyTypeFieldName != null ? !anomalyTypeFieldName.equals(evidence.anomalyTypeFieldName) : evidence.anomalyTypeFieldName != null)
			return false;
		return evidenceType == evidence.evidenceType;

	}

	@Override
	public int hashCode() {
		if (this.entityType == null && entityName == null && startDate == null && endDate == null
				&& anomalyTypeFieldName == null && evidenceType == null) {
			// if all data members composing the key of the evidence are null, use the same hashcode of the super method.
			// this case is used when we have a reference to an existing evidence.
			return super.hashCode();
		}

		int result = entityType != null ? entityType.hashCode() : 0;
		result = 31 * result + (entityName != null ? entityName.hashCode() : 0);
		result = 31 * result + (startDate != null ? startDate.hashCode() : 0);
		result = 31 * result + (endDate != null ? endDate.hashCode() : 0);
		result = 31 * result + (anomalyTypeFieldName != null ? anomalyTypeFieldName.hashCode() : 0);
		result = 31 * result + (evidenceType != null ? evidenceType.hashCode() : 0);

		return result;
	}



	@Override public String toString() {
		return "{" +
				"id=" + super.getId() +
				", entityType=" + entityType +
				", entityName='" + entityName + '\'' +
				", startDate=" + startDate +
				", endDate=" + endDate +
				", anomalyTypeFieldName='" + anomalyTypeFieldName + '\'' +
				'}';
	}
}



