package fortscale.domain.core;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = UserActivityNetworkAuthentication.COLLECTION_NAME)
@CompoundIndexes({
		@CompoundIndex(name = "user_start_time", def = "{'normalizedUsername': -1, 'startTime': 1}")
})
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserActivityNetworkAuthentication extends UserActivityDocument {

	public static final String COLLECTION_NAME = "user_activity_network_authentication";

}
