package fortscale.domain.core;

import java.io.Serializable;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = NotificationResource.COLLECTION_NAME)
public class NotificationResource extends AbstractDocument implements Serializable {

	private static final long serialVersionUID = -4341743756171863765L;

	public static final String COLLECTION_NAME = "notification_resources";

	@Indexed(unique=true)
	private String msg_name;
	private String single;
	private String agg;

	public NotificationResource() {}
	
	public NotificationResource(String msg_name, String single, String agg) {
		this.msg_name = msg_name;
		this.single = single;
		this.agg = agg;
	}
	
	public String getMsg_name() {
		return msg_name;
	}

	public String getSingle() {
		return single;
	}

	public String getAgg() {
		return agg;
	}

}