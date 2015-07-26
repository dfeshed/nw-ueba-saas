package fortscale.domain.core;

import org.springframework.data.mongodb.core.mapping.Document;
import java.io.Serializable;

/**
 * Created by Amir Keren on 26/07/2015.
 */
@Document(collection = StatefulInternalStash.COLLECTION_NAME)
public class StatefulInternalStash extends AbstractDocument implements Serializable {

	public static final String COLLECTION_NAME = "stateful_internal_stash";
	public static final String SUUID = "NotificationToEvidence";
	public static final String SUUID_FIELD = "suuid";
	public static final String LATEST_TS_FIELD = "latest_ts";

	private static final long serialVersionUID = 8205143205220903382L;

	private long latest_ts;
	private String suuid;
	private long timestamp;

	public StatefulInternalStash() {}

	public StatefulInternalStash(long latest_ts, String suuid, long timestamp) {
		this.latest_ts = latest_ts;
		this.suuid = suuid;
		this.timestamp = timestamp;
	}

	public long getLatest_ts() {
		return latest_ts;
	}

	public void setLatest_ts(long latest_ts) {
		this.latest_ts = latest_ts;
	}

	public String getSuuid() {
		return suuid;
	}

	public void setSuuid(String suuid) {
		this.suuid = suuid;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

}