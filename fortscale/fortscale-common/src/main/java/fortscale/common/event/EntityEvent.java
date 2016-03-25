package fortscale.common.event;

import net.minidev.json.JSONObject;
import org.springframework.util.Assert;

import java.util.Map;

public class EntityEvent extends MultiContextFieldsEvent {
	public EntityEvent(JSONObject jsonObject, String dataSource) {
		super(jsonObject, dataSource);
	}
}
