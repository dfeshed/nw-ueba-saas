package fortscale.domain.core;

import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

/**
 * Represents single evidence in MongoDB
 *
 * More information: https://fortscale.atlassian.net/wiki/display/FSC/Evidence+Collection+in+MongoDB
 *
 * Date: 6/22/2015.
 */
@Document(collection = Evidence.COLLECTION_NAME)
public class Evidence extends AbstractDocument{

	/**
	 * Collection Name
	 */
	public static final String COLLECTION_NAME = "evidences";


	//-- Document's Field Names


	// Entity information
	public static final String entityTypeField = "entityType";
	public static final String entityNameField = "entityName";
	public static final String entityMongoIdField = "entityMongoId";

	// Time frame information
	public static final String startDateField = "startDate";
	public static final String endDateField = "endDate";
	public static final String retentionDateField = "retentionDate";

	// attributes
	public static final String typeField = "type";
	public static final String dataSourceField = "dataSource";

	// severity and status
	public static final String scoreField = "score";
	public static final String severityField = "severity";
	public static final String statusField = "status";




	//-- Document's Fields


	@Field(entityTypeField)
	private String entityType;

	@Field(entityNameField)
	private String entityName;

	@Indexed(unique=false, direction= IndexDirection.ASCENDING)
	@Field(entityMongoIdField)
	private String entityMongoId;

	@Field(startDateField)
	private Date startDate;

	@Field(endDateField)
	private Date endDate;

	// Expiration: one year
	@Indexed(expireAfterSeconds = 31536000)
	@Field(retentionDateField)
	private Date retentionDate;

	@Field(typeField)
	private String type;

	@Field(dataSourceField)
	private String dataSource;

	@Field(scoreField)
	private Integer score;

	@Field(severityField)
	private EvidenceSeverity severity;


	// C-tor

	public Evidence(String entityType, String entityName, String entityMongoId, Date startDate, Date endDate,
			String type, String dataSource, Integer score, EvidenceSeverity severity) {
		this.entityType = entityType;
		this.entityName = entityName;
		this.entityMongoId = entityMongoId;
		this.startDate = startDate;
		this.endDate = endDate;
		this.type = type;
		this.dataSource = dataSource;
		this.score = score;
		this.severity = severity;

		// set retention to start date
		this.retentionDate = startDate;
	}

	// Setters

	public void setRetentionDate(Date retentionDate) {
		this.retentionDate = retentionDate;
	}

	// Getters

	public String getEntityType() {
		return entityType;
	}

	public String getEntityName() {
		return entityName;
	}

	public String getEntityMongoId() {
		return entityMongoId;
	}

	public Date getStartDate() {
		return startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public String getType() {
		return type;
	}

	public Integer getScore() {
		return score;
	}

	public EvidenceSeverity getSeverity() {
		return severity;
	}

	public String getDataSource() {
		return dataSource;
	}
}



