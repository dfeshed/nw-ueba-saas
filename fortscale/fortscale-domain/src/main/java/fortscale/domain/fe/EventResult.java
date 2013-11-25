package fortscale.domain.fe;

import java.util.Map;

import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import fortscale.domain.core.AbstractDocument;




@Document(collection=EventResult.collectionName)
public class EventResult  extends AbstractDocument{
	public static final String collectionName =  "event_result";
	public static final String sqlQueryField = "sqlQuery";
	public static final String globalScoreField = "globalScore";
	public static final String eventScoreField = "eventScore";
	public static final String eventTimeField = "eventTime";
	public static final String lastRetrievedField = "lastRetrieved";
	public static final String attributesField = "attributes";
	public static final String CREATED_AT_FIELD_NAME = "createdAt";
	
	
	@Indexed
	@Field(sqlQueryField)
	private String sqlQuery;
	
	@Field(globalScoreField)
	private Double globalScore;
	
	private int total;
	
	@Indexed
	@Field(eventScoreField)
	private Double eventScore;
	
	@Indexed
	@Field(eventTimeField)
	private DateTime eventTime;
	
	
	@CreatedDate
	@Indexed
    @Field(CREATED_AT_FIELD_NAME)
    private DateTime createdAt;
	
	@Indexed(unique = false, expireAfterSeconds=60*30)
	@Field(lastRetrievedField)
	private DateTime lastRetrieved;
	
	@Field(attributesField)
	private Map<String, Object> attributes;

	public String getSqlQuery() {
		return sqlQuery;
	}

	public void setSqlQuery(String sqlQuery) {
		this.sqlQuery = sqlQuery;
	}

	public Double getGlobalScore() {
		return globalScore;
	}

	public void setGlobalScore(Double globalScore) {
		this.globalScore = globalScore;
	}

	public DateTime getLastRetrieved() {
		return lastRetrieved;
	}

	public void setLastRetrieved(DateTime lastRetrieved) {
		this.lastRetrieved = lastRetrieved;
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}
	
	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}
	
	public DateTime getCreatedAt() {
		return createdAt;
	}
	
	

	public Double getEventScore() {
		return eventScore;
	}

	public void setEventScore(Double eventScore) {
		this.eventScore = eventScore;
	}

	public DateTime getEventTime() {
		return eventTime;
	}

	public void setEventTime(DateTime eventTime) {
		this.eventTime = eventTime;
	}

	public static String getAttributesAttributeNameField(String attributeName) {
		return String.format("%s.%s", attributesField,attributeName);
	}
}
