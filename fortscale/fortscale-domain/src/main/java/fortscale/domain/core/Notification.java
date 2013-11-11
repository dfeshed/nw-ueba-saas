package fortscale.domain.core;

import java.io.Serializable;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = Notification.COLLECTION_NAME)
public class Notification extends AbstractDocument implements Serializable {
	public static final String COLLECTION_NAME = "notifications";

	private long ts;
	private String generator_name;
	private String name;
	private String cause;
	private String displayName;
	private String uuid;
	private String fsId;

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

	public String getUuid() {
		return uuid;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getFsId() {
		return fsId;
	}

}