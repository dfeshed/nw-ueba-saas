package fortscale.domain.fetch;

import fortscale.domain.core.AbstractAuditableDocument;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import static com.google.common.base.Preconditions.checkNotNull;

@Document(collection="fetch_configuration")
public class FetchConfiguration extends AbstractAuditableDocument {


	// uses to holds the data source to get last fetch time
	@Indexed(unique=true)
	private String type;

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
