package fortscale.common.event;

import net.minidev.json.JSONObject;
import org.springframework.util.Assert;

public class EntityEvent implements Event {
	private JSONObject message;
	private String dataSource;

	public EntityEvent(JSONObject message, String dataSource) {
		Assert.notNull(message);
		Assert.notNull(dataSource);
		this.message = message;
		this.dataSource = dataSource;
	}

	@Override
	public Object get(String key) {
		return message.get(key);
	}

	@Override
	public JSONObject getJSONObject() {
		return message;
	}

	@Override
	public String getDataSource() {
		return dataSource;
	}
}
