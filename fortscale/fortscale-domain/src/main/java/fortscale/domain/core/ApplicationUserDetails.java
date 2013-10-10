package fortscale.domain.core;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.util.Assert;

public class ApplicationUserDetails {
	@JsonProperty
	private String applicationName;
	@JsonProperty
	private String userName;
	
	@PersistenceConstructor
	@JsonCreator
	public ApplicationUserDetails(@JsonProperty("applicationName") String applicationName, @JsonProperty("userName") String userName) {
		Assert.hasText(applicationName);
		Assert.hasText(userName);

		this.userName = userName;
		this.applicationName = applicationName;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public String getUserName() {
		return userName;
	}
	
	
}
