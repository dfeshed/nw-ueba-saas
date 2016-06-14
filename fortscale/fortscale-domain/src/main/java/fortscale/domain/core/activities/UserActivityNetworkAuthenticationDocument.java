package fortscale.domain.core.activities;


import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.HashMap;
import java.util.Map;

@Document(collection = UserActivityNetworkAuthenticationDocument.COLLECTION_NAME)
public class UserActivityNetworkAuthenticationDocument extends UserActivityDocument {

	public static final String COLLECTION_NAME = "user_activity_network_authentication";
	public static final String FIELD_NAME_AUTHENTICATIONS = "authentications";
	public static final String FIELD_NAME_AUTHENTICATIONS_HISTOGRAM = "authenticationsHistogram";
	public static final String FIELD_NAME_HISTOGRAM_SUCCESSES = "successes";
	public static final String FIELD_NAME_HISTOGRAM_FAILURES = "failures";

	@Field(FIELD_NAME_AUTHENTICATIONS)
	private Authentications authentications = new Authentications();

	public Authentications getAuthentications() {
		return authentications;
	}

	@Override
	public Map<String, Double> getHistogram() {
		return getAuthentications().getAuthenticationsHistogram();
	}

	public static class Authentications {

		private Map<String, Double> authenticationsHistogram = new HashMap<>();

		@Field(AUTHENTICATIONS_HISTOGRAM_FIELD_NAME)
		public Map<String, Double> getAuthenticationsHistogram() {
			return authenticationsHistogram;
		}
	}

}