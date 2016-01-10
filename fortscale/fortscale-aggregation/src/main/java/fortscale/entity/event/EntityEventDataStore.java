package fortscale.entity.event;

import java.util.Date;
import java.util.List;

public interface EntityEventDataStore {
	EntityEventData getEntityEventData(String entityEventName, String contextId, long startTime, long endTime);
	List<EntityEventData> getEntityEventDataWithModifiedAtEpochtimeLte(String entityEventName, long modifiedAtEpochtime);
	List<EntityEventData> getEntityEventDataWithModifiedAtEpochtimeLteThatWereNotTransmitted(String entityEventName, long modifiedAtEpochtime);
	List<EntityEventData> getEntityEventDataWithEndTimeInRange(String entityEventName, Date fromTime, Date toTime);
	void storeEntityEventData(EntityEventData entityEventData);
}
