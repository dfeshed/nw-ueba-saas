package fortscale.entity.event;

import org.springframework.data.domain.PageRequest;

import java.util.Date;
import java.util.List;

public interface EntityEventDataStore {
	public EntityEventData getEntityEventData(String entityEventName, String contextId, long startTime, long endTime);
	public List<EntityEventData> getEntityEventDataWithModifiedAtEpochtimeLte(String entityEventName, long modifiedAtEpochtime);
	public List<EntityEventData> getEntityEventDataThatWereNotTransmitted(String entityEventName, PageRequest pageRequest);
	public List<EntityEventData> getEntityEventDataWithEndTimeInRange(String entityEventName, Date fromTime, Date toTime);
	public void storeEntityEventData(EntityEventData entityEventData);
}
