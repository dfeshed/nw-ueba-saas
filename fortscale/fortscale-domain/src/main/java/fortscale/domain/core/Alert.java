package fortscale.domain.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Map;

/**
 * This is the bean of Alert entity that is saved in Alerts collection in MongoDB
 * See https://fortscale.atlassian.net/wiki/display/FSC/Alert+Collection+in+MongoDB
 */
@Document(collection = Alert.COLLECTION_NAME)
@CompoundIndexes({
	@CompoundIndex(name="entity_type_entity_name_desc", def = "{'entityType': 1, 'entityName': -1}"),
})
public class Alert extends AbstractDocument implements Serializable {



	private static final long serialVersionUID = -8514041678913795872L;
	public static final String COLLECTION_NAME = "alerts";

	//Fields names
	public static final String uuidField = "uuid";
	public static final String startDateField = "startDate";
	public static final String endDateField = "endDate";
	public static final String entityTypeField = "entityType";
	public static final String entityNameField = "entityName";
	public static final String ruleField = "rule";
	public static final String evidencesField = "evidences";
	public static final String causeField = "cause";
	public static final String scoreField = "score";
	public static final String severityField = "severity";
	public static final String statusField = "status";
	public static final String commentField = "comment";

	//document's fields
	@JsonIgnore
	@Indexed(unique=true)
	@Field(uuidField)
	private String uuid;
	@Indexed(unique=false)
	@Field(startDateField)
	private long startDate;
	@Indexed(unique=false)
	@Field(endDateField)
	private long endDate;
	@Field(entityTypeField)
	private EntityType entityType;
	@Field(entityNameField)
	private String entityName;
	@Field(ruleField)
	private String rule;
	@Field(evidencesField)
	private Map<String, String> evidences;
	@Field(causeField)
	private String cause;
	@Field(scoreField)
	private Integer score;
	@Field(severityField)
	private Severity severity;
	@Field(statusField)
	private AlertStatus status;
	@Field(commentField)
	private String comment;

	public Alert() {}

	public Alert(String uuid, long startDate, long endDate, EntityType entityType, String entityName, String rule, Map<String, String> evidences, String cause, int score, Severity severity, AlertStatus status, String comment) {
		this.uuid = uuid;
		this.startDate = startDate;
		this.endDate = endDate;
		this.entityType = entityType;
		this.entityName = entityName;
		this.rule = rule;
		this.evidences = evidences;
		this.cause = cause;
		this.score = score;
		this.severity = severity;
		this.status = status;
		this.comment = comment;
		this.setId(System.currentTimeMillis() + entityName + entityType);
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public long getStartDate() {
		return startDate;
	}

	public void setStartDate(long startDate) {
		this.startDate = startDate;
	}

	public long getEndDate() {
		return endDate;
	}

	public void setEndDate(long endDate) {
		this.endDate = endDate;
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

	public String getRule() {
		return rule;
	}

	public void setRule(String rule) {
		this.rule = rule;
	}

	public Map<String, String> getEvidences() {
		return evidences;
	}

	public void setEvidences(Map<String, String> evidences) {
		this.evidences = evidences;
	}

	public String getCause() {
		return cause;
	}

	public void setCause(String cause) {
		this.cause = cause;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public Severity getSeverity() {
		return severity;
	}

	public void setSeverity(Severity severity) {
		this.severity = severity;
	}

	public AlertStatus getStatus() {
		return status;
	}

	public void setStatus(AlertStatus status) {
		this.status = status;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
}