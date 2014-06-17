package fortscale.domain.streaming.user;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import fortscale.domain.core.AbstractAuditableDocument;

@Document(collection=UserScoreSnapshot.TABLE_NAME)
public class UserScoreSnapshot extends AbstractAuditableDocument{
	private static final long serialVersionUID = 1L;
	
	public static final String TABLE_NAME = "streaming_userscore_snapshot";
	
	public static final String CLASSIFIER_ID_FIELD = "classifierId";
	public static final String USER_NAME_FIELD = "username";
	public static final String SNAPSHOT = "snapshot";
	
	@Field(CLASSIFIER_ID_FIELD)
	private String classifierId;
	@Indexed
	@Field(USER_NAME_FIELD)
	private String userName;
	@Field(SNAPSHOT)
	private Object snapshot;
	
	
	
	public String getClassifierId() {
		return classifierId;
	}
	public void setClassifierId(String classifierId) {
		this.classifierId = classifierId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public Object getSnapshot() {
		return snapshot;
	}
	public void setSnapshot(Object snapshot) {
		this.snapshot = snapshot;
	}
	
	
	
}
