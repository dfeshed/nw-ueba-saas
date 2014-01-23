package fortscale.domain.core;

import java.io.Serializable;
import java.util.Map;

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
	private String type;
	private Map<String, String> attributes;
	
	public Notification() {}
	
	public Notification(long ts, String generator_name, String name, String cause, String displayName, String uuid, String fsId, String type) {
		this.ts = ts;
		this.generator_name = generator_name;
		this.name = name;
		this.cause = cause;
		this.displayName = displayName;
		this.uuid = uuid;
		this.fsId = fsId;
		this.type = type;
	}
	
	public Map<String, String> getAttributes(){
		return attributes;
	}
	
	public void setCause(String cause) {
		this.cause = cause;
	}

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

	public String getType() {
		return type;
	}

}