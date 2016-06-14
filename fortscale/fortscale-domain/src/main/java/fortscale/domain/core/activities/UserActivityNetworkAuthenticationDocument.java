package fortscale.domain.core.activities;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.HashMap;
import java.util.Map;

@Document(collection = UserActivityNetworkAuthenticationDocument.COLLECTION_NAME)
@CompoundIndexes({
		@CompoundIndex(name = "user_start_time", def = "{'normalizedUsername': -1, 'startTime': 1}")
})
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserActivityNetworkAuthenticationDocument extends UserActivityDocument {

	public static final String COLLECTION_NAME = "user_activity_network_authentication";
	public static final String AUTHENTICATIONS_FIELD_NAME = "authentications";
	public static final String AUTHENTICATIONS_HISTOGRAM_FIELD_NAME = "authenticationsHistogram";
	public static final String FIELD_NAME_HISTOGRAM_SUCCESSES = "successes";
	public static final String FIELD_NAME_HISTOGRAM_FAILURES = "failures";

	@Field(AUTHENTICATIONS_FIELD_NAME)
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
