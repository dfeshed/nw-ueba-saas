package fortscale.domain.analyst;

import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.util.Assert;

import fortscale.domain.core.AbstractDocument;


@Document
public class AnalystFollowUser extends AbstractDocument{
	
	public static final String CREATED_AT_FIELD_NAME = "createdAt";
	
	@CreatedDate
    @Field(CREATED_AT_FIELD_NAME)
    private DateTime createdAt;
	
	private String analystId;
	
	private String analystUsername;
	
	private String userId;
	
	private String username;
	
	private Boolean follow;
	
	
	@PersistenceConstructor
	public AnalystFollowUser(String analystId, String analystUsername, String userId, String username, boolean follow) {
		Assert.hasText(analystId);
		Assert.hasText(analystUsername);
		Assert.hasText(userId);

		this.analystId = analystId;
		this.analystUsername = analystUsername;
		this.userId = userId;
		this.username = username;
		this.follow = follow;
	}

	public String getAnalystId() {
		return analystId;
	}

	public void setAnalystId(String analystId) {
		this.analystId = analystId;
	}

	public String getAnalystUsername() {
		return analystUsername;
	}

	public void setAnalystUsername(String analystUsername) {
		this.analystUsername = analystUsername;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public DateTime getCreatedAt() {
		return createdAt;
	}

	public Boolean getFollow() {
		return follow;
	}

	public void setFollow(Boolean follow) {
		this.follow = follow;
	}
	
	
	
}
