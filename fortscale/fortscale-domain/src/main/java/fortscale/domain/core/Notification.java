package fortscale.domain.core;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = Notification.COLLECTION_NAME)
public class Notification implements Serializable {
	public static final String COLLECTION_NAME = "notifications";
	@Id
	private String id;
	private long ts;
	private String generator_name;
	private String name;
	private String cause;

	public long getTs() {
		return ts;
	}

	public String getGenerator_name() {
		return generator_name;
	}

	public String getName() {
		return name;
	}

	public String getCause() {
		return cause;
	}

}