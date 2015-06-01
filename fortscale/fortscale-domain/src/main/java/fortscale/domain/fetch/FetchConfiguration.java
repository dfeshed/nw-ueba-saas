package fortscale.domain.fetch;

import fortscale.domain.core.AbstractAuditableDocument;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import static com.google.common.base.Preconditions.checkNotNull;

/*
 *
 * holds state of fetch progress for a <type> data source.
 * uses for managing fetches and avoiding the case of a miss fetch/fetch failures
 * this configuration is saved to mongo.
 *
 */
@Document(collection="fetch_configuration")
public class FetchConfiguration extends AbstractAuditableDocument {


	// use to holds the data source
	@Indexed(unique=true)
	private String type;

	// last fetch time
	private String lastFetchTime;


	public FetchConfiguration(String type) {
		checkNotNull(type);
		this.type = type;

	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLastFetchTime() {
		return lastFetchTime;
	}

	public void setLastFetchTime(String lastFetchTime) {
		this.lastFetchTime = lastFetchTime;
	}
}
