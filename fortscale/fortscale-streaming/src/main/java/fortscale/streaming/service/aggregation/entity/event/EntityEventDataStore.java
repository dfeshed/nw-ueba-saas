package fortscale.streaming.service.aggregation.entity.event;

import java.util.List;

public interface EntityEventDataStore {
	public EntityEventData getEntityEventData(String entityEventName, String contextId, long startTime, long endTime);
	public List<EntityEventData> getEntityEventDataWithFiringTimeLte(String entityEventName, long firingTimeInSeconds);
	public List<EntityEventData> getEntityEventDataWithFiringTimeLteThatWereNotFired(String entityEventName, long firingTimeInSeconds);
	public void storeEntityEventData(EntityEventData entityEventData);
	public void emptyEntityEventDataStore();
}
