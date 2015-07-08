package fortscale.domain.core;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

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
	@CompoundIndex(name="unique_evidence", def = "{'" + Evidence.startDateField + "': 1, '" + Evidence.endDateField +"': 1, '" + Evidence.entityTypeField +"': 1, '" + Evidence.entityNameField +"': 1, '" + Evidence.anomalyTypeField +"': 1}", unique = true)
})
public class Evidence extends AbstractDocument{

	/**
	 * Collection Name
	 */
	public static final String COLLECTION_NAME = "evidences";


	//-- Document's Field Names


	// Entity information
	public static final String entityTypeField = "entityType";
	public static final String entityNameField = "entityName";

	// Time frame information
	public static final String startDateField = "startDate";
	public static final String endDateField = "endDate";
	public static final String retentionDateField = "retentionDate";

	// attributes
	public static final String nameField = "name";
	public static final String anomalyTypeField = "anomalyType";
	public static final String anomalyValueField = "anomalyValue";
	public static final String dataSourceField = "dataSource";
	public static final String evidenceTypeField = "evidenceType";

	// The 3 top events
	public static final String top3eventsField = "top3eventsJsonStr";
	// number of events in evidence
	public static final String numOfEventsField = "numOfEvents";
	// supporting Information
	public static final String supportingInformationField = "supportingInformation";

	// severity and score
	public static final String scoreField = "score";
	public static final String severityField = "severity";





	//-- Document's Fields


	@Field(entityTypeField)
	private EntityType entityType;

	@Field(entityNameField)
	private String entityName;

	@Field(startDateField)
	private Long startDate;

	@Field(endDateField)
	private Long endDate;

	// Index for expiration (TTL): one year
	@Indexed(expireAfterSeconds = 31536000)
	@Field(retentionDateField)
	private Long retentionDate;

	@Field(anomalyTypeField)
	private String anomalyType;

	@Field(nameField)
	private String name;

	@Field(anomalyValueField)
	private String anomalyValue;

	@Field(dataSourceField)
	private String dataSource;

	@Field(evidenceTypeField)
	private EvidenceType evidenceType;

	@Field(scoreField)
	private Integer score;

	@Field(severityField)
	private Severity severity;

	@Field(top3eventsField)
	private String top3eventsJsonStr;

	@Field(numOfEventsField)
	private Integer numOfEvents;

	@Field(supportingInformationField)
	private EvidenceSupportingInformation supportingInformation = new EvidenceSupportingInformation();

	// C-tor

	public Evidence(EntityType entityType, String entityName, Long startDate, Long endDate, String anomalyType,
			String name, String anomalyValue, String dataSource, Integer score, Severity severity) {
		this.entityType = entityType;
		this.entityName = entityName;
		this.startDate = startDate;
		this.endDate = endDate;
		this.anomalyType = anomalyType;
		this.name = name;
		this.anomalyValue = anomalyValue;
		this.dataSource = dataSource;
		this.score = score;
		this.severity = severity;


		// set retention to start date
		this.retentionDate = startDate;

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

	public void setRetentionDate(Long retentionDate) {
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

	// Getters

	public EntityType getEntityType() {
		return entityType;
	}

	public String getEntityName() {
		return entityName;
	}

	public Long getStartDate() {
		return startDate;
	}

	public Long getEndDate() {
		return endDate;
	}

	public Long getRetentionDate() {
		return retentionDate;
	}

	public String getAnomalyType() {
		return anomalyType;
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

	public String getDataSource() {
		return dataSource;
	}

	public EvidenceSupportingInformation getSupportingInformation() {
		return supportingInformation;
	}

	public String getTop3eventsJsonStr() {
		return top3eventsJsonStr;
	}

	public String getAnomalyValue() {
		return anomalyValue;
	}

	public Integer getNumOfEvents() {
		return numOfEvents;
	}

	public EvidenceType getEvidenceType() {
		return evidenceType;
	}
}



