package fortscale.entity.event;

import java.util.Date;
import java.util.List;

public interface EntityEventDataStore {
	public EntityEventData getEntityEventData(String entityEventName, String contextId, long startTime, long endTime);
	public List<EntityEventData> getEntityEventDataWithModifiedAtEpochtimeLte(String entityEventName, long modifiedAtEpochtime);
	public List<EntityEventData> getEntityEventDataWithModifiedAtEpochtimeLteThatWereNotTransmitted(String entityEventName, long modifiedAtEpochtime);
	public List<EntityEventData> getEntityEventDataInTimeRange(String entityEventName, Date startTime, Date endTime);
	public void storeEntityEventData(EntityEventData entityEventData);
}
