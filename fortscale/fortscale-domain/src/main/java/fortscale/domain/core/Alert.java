package fortscale.domain.core;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

/**
 * This is the bean of Alert entity that is saved in Alerts collection in MongoDB
 * See https://fortscale.atlassian.net/wiki/display/FSC/Alert+Collection+in+MongoDB
 */
@Document(collection = Alert.COLLECTION_NAME)
@CompoundIndexes({ @CompoundIndex(name = "entity_type_entity_name_desc", def = "{'entityType': 1, 'entityName': -1}"), })
public class Alert extends AbstractDocument implements Serializable {

	private static final long serialVersionUID = -8514041678913795872L;
	public static final String COLLECTION_NAME = "alerts";

	//Fields names
	public static final String nameField = "name";
	public static final String startDateField = "startDate";
	public static final String endDateField = "endDate";
	public static final String entityTypeField = "entityType";
	public static final String entityNameField = "entityName";
	public static final String entityIdField = "entityId";
	public static final String ruleField = "rule";
	public static final String evidencesField = "evidences";
	public static final String evidencesSizeField = "indicatorsNum";
	public static final String scoreField = "score";
	public static final String severityField = "severity";
	public static final String statusField = "status";
	public static final String feedbackField = "feedback";
	public static final String commentField = "comment";
	public static final String severityCodeField = "severityCode";

	//document's fields
	@Field(nameField) private String name;

	@Indexed(unique = false) @Field(startDateField) private long startDate;

	@Indexed(unique = false) @Field(endDateField) private long endDate;
	@Field(entityTypeField) private EntityType entityType;
	@Field(entityNameField) private String entityName;
	@Field(entityIdField) private String entityId;
	@Field(evidencesField)
	//this annotation makes mongo to save only reference to evidences, not the evidences themselves.
	@DBRef private List<Evidence> evidences;
	@Field(evidencesSizeField) private Integer evidenceSize;
	@Field(scoreField) private Integer score;
	@Field(severityCodeField) private Integer severityCode;
	@Indexed(unique = false) @Field(severityField) private Severity severity;
	@Indexed(unique = false) @Field(statusField) private AlertStatus status;
	@Indexed(unique = false) @Field(feedbackField) private AlertFeedback feedback;
	@Field(commentField) private String comment;

	public Alert() {
	}

	public Alert(Alert alert) {
		this.name = alert.getName();
		this.startDate = alert.getStartDate();
		this.endDate = alert.getEndDate();
		this.entityType = alert.getEntityType();
		this.entityName = alert.getEntityName();
		this.evidences = alert.getEvidences();
		this.evidenceSize = alert.getEvidenceSize();
		this.score = alert.getScore();
		this.severity = alert.getSeverity();
		this.severityCode = this.severity.ordinal();
		this.status = alert.getStatus();
		this.feedback = alert.getFeedback();
		this.comment = alert.getComment();
		this.entityId = alert.getEntityId();
		this.setId(alert.getId());
	}

	public Alert(String name, long startDate, long endDate, EntityType entityType, String entityName,
			List<Evidence> evidences, int evidencesSize, int score, Severity severity, AlertStatus status,
			AlertFeedback feedback, String comment, String entityId) {
		this.name = name;
		this.startDate = startDate;
		this.endDate = endDate;
		this.entityType = entityType;
		this.entityName = entityName;
		this.evidences = evidences;
		this.evidenceSize = evidencesSize;
		this.score = score;
		this.severity = severity;
		this.severityCode = severity.ordinal();
		this.status = status;
		this.feedback = feedback;
		this.comment = comment;
		this.entityId = entityId;
		this.setId(UUID.randomUUID().toString());
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

	public List<Evidence> getEvidences() {
		return evidences;
	}

	public void setEvidences(List<Evidence> evidences) {
		this.evidences = evidences;
	}

	public Integer getEvidenceSize() {
		return evidenceSize;
	}

	public void setEvidenceSize(Integer evidenceSize) {
		this.evidenceSize = evidenceSize;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEntityId() {
		return entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public AlertFeedback getFeedback() {
		return feedback;
	}

	public void setFeedback(AlertFeedback feedback) {
		this.feedback = feedback;
	}

	public Integer getSeverityCode() {
		return severityCode;
	}

	public void setSeverityCode(Integer severityCode) {
		this.severityCode = severityCode;
	}

	@Override public String toString() {
		return toString(true);
	}

	public String toString(Boolean addIndicators) {
		StringBuilder value = new StringBuilder();
		value.append("Alert Name: " + name);
		value.append(" Start Time: " + startDate);
		value.append(" End Time: " + endDate);
		value.append(" Entity Name: " + entityName);
		value.append(" Entity Type: " + entityType.name());
		value.append(" Severity: " + severity.name());
		value.append(" Alert Status: " + status.name());
		value.append(" Comment: " + comment);
		if (addIndicators) {
			value.append("Indicators: " + convertIndicatorsToString());
		}

		return value.toString();
	}

	@Override public int hashCode() {
		return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
				append(name).
				append(startDate).
				append(endDate).
				append(entityType.name()).
				append(entityName).
				append(score).
				append(severity.name()).
				append(status.name()).
				append(comment).
				toHashCode();
	}

	private String convertIndicatorsToString() {
		StringBuilder indicators = new StringBuilder();
		for (Evidence evidence: evidences) {
			indicators.append(evidence.toString() + " ");
		}

		return indicators.toString();
	}

}