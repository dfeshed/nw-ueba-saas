package fortscale.domain.core;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
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
	@CompoundIndex(name="entity_idx", def = "{'" + Evidence.entityNameField + "': 1, '" + Evidence.entityTypeField +"': 1}", unique = false)
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
	public static final String typeField = "type";
	public static final String nameField = "name";
	public static final String dataSourceField = "dataSource";

	// The 3 top events
	public static final String top3eventsField = "top3eventsJsonStr";
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

	// Expiration: one year
	@Indexed(expireAfterSeconds = 31536000)
	@Field(retentionDateField)
	private Long retentionDate;

	@Field(typeField)
	private String type;

	@Field(nameField)
	private String name;

	@Field(dataSourceField)
	private String dataSource;

	@Field(scoreField)
	private Integer score;

	@Field(severityField)
	private Severity severity;

	@Field(top3eventsField)
	private String top3eventsJsonStr;


	@Field(supportingInformationField)
	private EvidenceSupportingInformation supportingInformation = new EvidenceSupportingInformation();

	// C-tor

	public Evidence(EntityType entityType, String entityName, Long startDate, Long endDate,
			String type, String name, String dataSource, Integer score, Severity severity) {
		this.entityType = entityType;
		this.entityName = entityName;
		this.startDate = startDate;
		this.endDate = endDate;
		this.type = type;
		this.name = name;
		this.dataSource = dataSource;
		this.score = score;
		this.severity = severity;

		// set retention to start date
		this.retentionDate = startDate;

		// We must create ID for the evidence so the alert can have reference to it
		this.setId(UUID.randomUUID().toString());
	}

	// For JSON serialization only
	public Evidence() {
	}

	// Setters

	public void setRetentionDate(Long retentionDate) {
		this.retentionDate = retentionDate;
	}

	public void setTop3eventsJsonStr(String top3eventsJsonStr) {
		this.top3eventsJsonStr = top3eventsJsonStr;
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

	public String getType() {
		return type;
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

}



