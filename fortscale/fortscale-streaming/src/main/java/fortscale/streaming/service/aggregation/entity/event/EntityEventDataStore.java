package fortscale.streaming.service.aggregation.entity.event;

import java.util.List;

public interface EntityEventDataStore {
	public EntityEventData getEntityEventData(String entityEventName, String contextId, long startTime, long endTime);
	public List<EntityEventData> getEntityEventData(String entityEventName, long firingTimeInSeconds);
	public void storeEntityEventData(EntityEventData entityEventData);
}
